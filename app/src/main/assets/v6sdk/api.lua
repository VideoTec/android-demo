--
-- Created by IntelliJ IDEA.
-- User: PanYing
-- Date: 16/3/2
-- Time: 下午4:10
-- To change this template use File | Settings | File Templates.
--
local protobuf = require "protobuf"
local log = require "log"
local copas = require "copas"
local mqtt_client = require "mqtt_client"
local mqtt = require "mqtt-ngcc-v6"
local hex = require "hex-toolkit"

--- @type callbacks
-- @field state_handler
-- @field receive_message
-- @field tx_response_callback
callbacks = setmetatable({}, {
  __index = function(_, fun_name)
    return function(...)
      log.warn("invoke empty callback '%s'", fun_name)
    end
  end
})

local proto = {}

local Session = { all = {} }

local Transaction = { all = {} }

local CODING_TYPE = mqtt.CODING_TYPE.PROTOBUF2

---------------------------------------------------------------------
--- Proto
---------------------------------------------------------------------

function proto.encode(proto_name, obj)
  local bytes
  local success, err_info = pcall(function()
    bytes = protobuf.encode(proto_name, obj)
  end)
  if success then
    return bytes
  else
    local info = string.format("encode protobuf '%s' fault", proto_name)
    if err_info then
      info = info .. " >> " .. err_info
    end
    log.error(debug.traceback())
    error(info, 0)
  end
end

function proto.decode(proto_name, bytes)
  local result
  local success, err_info = pcall(function()
    result = protobuf.decode(proto_name, bytes)
    if not result then
      error(protobuf.lasterror(), 0)
    end
  end)
  if success then
    return result
  else
    local info = string.format("decode protobuf '%s' fault", proto_name)
    if err_info then
      info = info .. " >> " .. err_info
    end
    log.error(debug.traceback())
    error(info, 0)
  end
end

function proto.rebuild_client_publish_message(cpm)
  return {
    messageType = cpm.messageType,
    options = cpm.options,
    content = proto.rebuild_content(cpm.content),
    receivers = proto.rebuild_list(cpm.receivers, proto.rebuild_receiver)
  }
end

function proto.rebuild_client_publish_report(cpr)
  return {
    receivedMsgId = cpr.receivedMsgId
  }
end

function proto.rebuild_server_publish(sp)
  return {
    lastMsgId = sp.lastMsgId,
    beginMsgId = sp.beginMsgId,
    msgCount = sp.msgCount,
    messages = proto.rebuild_list(sp.messages, proto.rebuild_message),
    dialogs = proto.rebuild_list(sp.dialogs, proto.rebuild_dialog),
    lastMsgId = sp.lastMsgId,
    lastMsgId = sp.lastMsgId,
  }
end

function proto.rebuild_message(msg)
  if msg then
    return {
      msgId = msg.msgId,
      ownerId = msg.ownerId,
      ownerEpid = msg.ownerEpid,
      time = msg.time,
      direction = msg.direction,
      peerUri = msg.peerUri,
      messageType = msg.messageType,
      options = msg.options,
      content = proto.rebuild_content(msg.content),
    }
  end
end

function proto.rebuild_dialog(dialog)
  if dialog then
    return {
      peerUri = dialog.peerUri,
      undeliveredCount = dialog.undeliveredCount,
      lastMsgId = dialog.lastMsgId,
      lastReadableMsgId = dialog.lastReadableMsgId,
      lastReadableMsgSummary = dialog.lastReadableMsgSummary,
      readCount = dialog.readCount,
      lastReadId = dialog.lastReadId,
    }
  end
end

function proto.rebuild_content(content)
  if content then
    return {
      uuid = content.uuid,
      fromUri = content.fromUri,
      fromEpid = content.fromEpid,
      fromNickname = content.fromNickname,
      contentType = content.contentType,
      contentBuffer = content.contentBuffer,
      summary = content.summary,
    }
  end
end

function proto.rebuild_receiver(receiver)
  if receiver then
    return {
      toUri = receiver.toUri,
      toEpid = receiver.toEpid,
    }
  end
end

function proto.rebuild_list(receivers, builder)
  if receivers then
    local result = {}
    for _, receiver in ipairs(receivers) do
      table.insert(result, builder(receiver))
    end
    return result
  end
end

---------------------------------------------------------------------
--- Session
---------------------------------------------------------------------
function Session.create()
  local session = setmetatable({}, {
    __index = function(self, key)
      if key == "mqtt_client" then
        self.mqtt_client = mqtt_client.create()
        self.mqtt_client.coding_type = CODING_TYPE
        -- set mqtt packet processor
        self.mqtt_client.packet_processor = function(packet)
          if packet.type == mqtt.TYPE.PUBLISH then
            local publish = proto.decode("ServerPublish", packet.message)
            publish = proto.rebuild_server_publish(publish)
            publish.topic = packet.topic
			log.info("publish.topic: '%s'", publish.topic);
            callbacks.receive_message(self.id, packet.message_type, proto.encode("ServerPublish", publish))
          end
        end
        self.mqtt_client.state_handler = function(new_status, reason, old_state, ...)
          callbacks.state_handler(self.id, new_status.code)
        end
        return self.mqtt_client
      else
        return Session[key]
      end
    end,
    __tostring = function(self)
      return string.format("Session{ id=%s }", tostring(self.id))
    end
  })
  table.insert(Session.all, session)
  session.id = #Session.all
  return session
end

function Session.get(session_id)
  local session = Session.all[session_id]
  if session then
    return session
  else
    return nil, "can't found session by id=" .. session_id
  end
end

function Session:configure(configuration)
  self.conf = configuration
end

function Session:create_tx(tx_id, tx_action, args)
  return Transaction.create(self, tx_id, args, tx_action.processor)
end

function Session:close()
  if self.mqtt_client then
    self.mqtt_client:disconnect()
  end
end


---------------------------------------------------------------------
--- Transaction (Tx)
---------------------------------------------------------------------
function Transaction.create(session, tx_id, args, processor)
  local tx = setmetatable({}, {
    __index = Transaction,
    __tostring = function(self)
      return string.format("Tx{ id=%s }", tostring(self.id))
    end
  })
  Transaction.all[tx_id] = tx
  tx.session = session
  tx.id = tx_id
  tx.args = args
  tx.processor = processor
  return tx
end

function Transaction.take(tx_id)
  local tx = Transaction.all[tx_id]
  if tx then
    Transaction.all[tx_id] = nil
    return tx
  else
    return nil, "can't found tx by id=" .. tx_id
  end
end

function Transaction:invoke()
  copas.addthread(function()
    self:processor()
  end)
end


---------------------------------------------------------------------
--- TX Action
---------------------------------------------------------------------
local tx_actions = {}

tx_actions.connect = {
  args_name = "ConnectArgs",
  processor = function(tx)
    local conn_args = {
      client_id = tx.args.epid,
      username = tostring(tx.args.userId),
      password = tx.args.token,
    }
    --    -- set mqtt client state handler
    --    tx.session.mqtt_client.state_handler = function(new_status, reason, old_state, ...)
    --      callbacks.state_handler(tx.session.id, new_status.code)
    --      -- 断线自动重连机制
    --      if new_status == mqtt_client.STATE.CONNECT_FAILED then
    --        local connack = select(1, ...)
    --        -- 貌似有 return_code 的理由都不能靠重试连接解决
    --        if not connack then
    --          -- TODO 这里是否需要有一个最大重试次数的判断以及重试间隔的判断,另外代码优点重复回头重构一下
    --          copas.addthread(function()
    --            copas.sleep(1)
    --            tx.session.mqtt_client:connect(tx.session.conf.mqttHost, tx.session.conf.mqttPort, conn_args)
    --          end)
    --        end
    --      elseif new_status == mqtt_client.STATE.DISCONNECTED then
    --        -- TODO 这里是否需要有一个最大重试次数的判断以及重试间隔的判断,另外代码优点重复回头重构一下
    --        copas.addthread(function()
    --          copas.sleep(3)
    --          tx.session.mqtt_client:connect(tx.session.conf.mqttHost, tx.session.conf.mqttPort, conn_args)
    --        end)
    --      end
    --    end

    -- connect callback
    local conn_callback
    conn_callback = function(success, err_info, connack)
      if success then
        callbacks.tx_response_callback(tx.id, proto.encode("BasicResult", { code = 200 }))
      elseif connack.content then
        local ack_content = proto.decode("ConnectAck", connack.content)
        if ack_content.code == 301 then
          local host, port = string.match(ack_content.redirectServer, "(.+):(%d+)")
          tx.session.mqtt_client:connect(host, tonumber(port), conn_args, conn_callback)
        else
          callbacks.tx_response_callback(tx.id, connack.content)
        end
      else
        local result = { reason = err_info }
        if connack then
          if connack.return_code == mqtt.CONNACK.RETURN_CODE.UNACCEPTABLE_PROTOCOL_VERSION then
            result.code = 500
          elseif connack.return_code == mqtt.CONNACK.RETURN_CODE.IDENTIFIER_REJECTED then
            result.code = 403
          elseif connack.return_code == mqtt.CONNACK.RETURN_CODE.SERVER_UNAVAILABLE then
            result.code = 501
          elseif connack.return_code == mqtt.CONNACK.RETURN_CODE.BAD_USER_NAME_OR_PASSWORD then
            result.code = 401
          elseif connack.return_code == mqtt.CONNACK.RETURN_CODE.NOT_AUTHORIZED then
            result.code = 401
          else
            result.code = 500
          end
        else
          result.code = 0
        end
        callbacks.tx_response_callback(tx.id, proto.encode("BasicResult", result))
      end
    end
    -- connect
    tx.session.mqtt_client:connect(tx.session.conf.mqttHost, tx.session.conf.mqttPort, conn_args, conn_callback)
  end
}

function tx_actions._get_publish_callback(tx)
  return function(success, ack_packet, err_info)
    local result
    if success then
      if ack_packet.content then
        result = ack_packet.content
      else
        result = proto.encode("ClientPublishAck", { code=200 })
      end
    else
      result = proto.encode("ClientPublishAck", { code=500, reason=err_info })
    end
    callbacks.tx_response_callback(tx.id, result)
  end
end

tx_actions.send_message = {
  args_name = "ClientPublishMessage",
  processor = function(tx)
    local publish_packet = {
      qos = 1,
      retain = false,
      topic = tx.args.topic, -- topic name (String)
      message_type = 1,
      message = proto.encode("ClientPublishMessage", proto.rebuild_client_publish_message(tx.args)) -- message (bytes)}
    }

    tx.session.mqtt_client:send_publish(publish_packet, tx_actions._get_publish_callback(tx))
  end
}

tx_actions.send_report = {
  args_name = "ClientPublishReport",
  processor = function(tx)
    local publish_packet = {
      qos = 1,
      retain = false,
      topic = tx.args.topic, -- topic name (String)
      message_type = 2,
      message = proto.encode("ClientPublishReport", proto.rebuild_client_publish_report(tx.args)) -- message (bytes)}
    }

    tx.session.mqtt_client:send_publish(publish_packet, tx_actions._get_publish_callback(tx))
  end
}

tx_actions.subscribe = {
  args_name = "SubscribeArgs",
  processor = function(tx)
    local subscribe_packet = { subscribes = {} }
    for _, topic in ipairs(tx.args.topics) do
      table.insert(subscribe_packet.subscribes, { qos = 0, topic = topic })
    end
    local callback = function(success, ack_packet, err_info)
      local result
      if success then
        for _, return_code in ipairs(ack_packet.return_codes) do
          if return_code == 0x80 then
            result = proto.encode("BasicResult", { code=400, reason='some subscribe fault.' })
            break
          end
        end
        if not result then
          result = proto.encode("BasicResult", { code=200 })
        end
      else
        result = proto.encode("BasicResult", { code=500, reason=err_info })
      end
      callbacks.tx_response_callback(tx.id, result)
    end
    tx.session.mqtt_client:send_subscribe(subscribe_packet, callback)
  end
}

tx_actions.unsubscribe = {
  args_name = "UnsubscribeArgs",
  processor = function(tx)
    local unsubscribe_packet = { topic_filters = tx.args }
    local callback = function(success, ack_packet, err_info)
      local result
      if success then
        result = proto.encode("BasicResult", { code=200 })
      else
        result = proto.encode("BasicResult", { code=500, reason=err_info })
      end
      callbacks.tx_response_callback(tx.id, result)
    end
    tx.session.mqtt_client:send_unsubscribe(unsubscribe_packet, callback)
  end
}

---------------------------------------------------------------------
--- utils
---------------------------------------------------------------------

local reg_pb = function(path)
  local f = assert(io.open(path, "rb"))
  local buffer = f:read "*a"
  f:close()
  log.info(path .. " loaded.")

  protobuf.register(buffer)
  log.info(path .. " registered.")
end

---------------------------------------------------------------------
--- sdk
---------------------------------------------------------------------
function sdk_init(path)
  -- register proto file load demo2.pb
  reg_pb(path .. "/api.pb")
  reg_pb(path .. "/v6.pb")
end


function sdk_session_new()
  return Session.create().id
end


function sdk_session_configure(session_id, config_buffer)
  local configuration = proto.decode("SessionConfiguration", config_buffer)
  Session.get(session_id):configure(configuration)
end


function sdk_session_connect(tx_id)
  sdk_tx_begin(tx_id)
end


function sdk_session_subscribe(tx_id)
  sdk_tx_begin(tx_id)
end


for key, tx_action in pairs(tx_actions) do
  if '_' ~= string.sub(key, 1,1) then
    local fun_name = "sdk_tx_new_" .. key
    _G[fun_name] = function(session_id, tx_id, args_buffer)
      log.debug("invoke '%s' session_id=%s tx_id=%s", fun_name, session_id, tx_id)
      local args = proto.decode(tx_action.args_name, args_buffer)
      assert(Session.get(session_id)):create_tx(tx_id, tx_action, args)
    end
  end
end


function sdk_tx_begin(tx_id)
  log.debug("invoke 'sdk_tx_begin' tx_id=%s", tx_id)
  local tx = assert(Transaction.take(tx_id))
  copas.addthread(function()
    tx:invoke()
  end)
end


function sdk_session_close(session_id)
  log.debug("invoke 'sdk_session_close' session_id=%s", session_id)
  assert(Session.get(session_id)):close()
end


function sdk_step(timeout)
  -- print("copas.step", timeout)
  if (copas.step(1.0 * timeout / 1000)) then
      return 0
  else 
      -- print("copas.step failed")
      return -1
  end
end
