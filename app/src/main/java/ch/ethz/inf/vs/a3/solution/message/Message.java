package ch.ethz.inf.vs.a3.solution.message;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joel on 24.10.2016.
 */

public class Message {
    public MessageHeader header;
    public JSONObject body;

    /// Create a message with an empty body and a header with empty fields
    public Message(){
        header = new MessageHeader("","","","");
        body = new JSONObject();
    }

    /// Create message from JSON Object
    public Message(JSONObject obj){
        try {
            this.header = new MessageHeader(obj.getJSONObject("header"));
            this.body = obj.getJSONObject("body");
        } catch(Exception e) {e.printStackTrace();}
    }

    /// Create from String representing a JSONObject
    public Message(String s){
        this(getJSONRObjectForConstructor(s));
    }

    /// Completely dumb workaround, but apparently to call one constructor from another the call
    /// needs to be on the first line and of course may only call static methods
    private static JSONObject getJSONRObjectForConstructor (String s) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = new JSONObject();
        }
        return jsonObject;
    }

    public void set_header(String username, String uuid, String timestamp, String type){
        header = new MessageHeader(username, uuid, timestamp, type);
    }

    public JSONObject get_json(){
        JSONObject obj = new JSONObject();
        try{
            obj.put("header", header.get_json());
            obj.put("body", body);
        }catch (Exception e){
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public String toString(){
        return get_json().toString();
    }

    public class MessageHeader {
        public String username;
        public String uuid;
        public String timestamp;
        public String type;
        MessageHeader(String username, String uuid, String timestamp, String type) {
            this.username = username;
            this.uuid = uuid;
            this.timestamp = timestamp;
            this.type = type;
        }
        public JSONObject get_json(){
            JSONObject obj = new JSONObject();
            try {
                obj.put("username", username);
                obj.put("uuid", uuid);
                obj.put("timestamp", timestamp);
                obj.put("type", type);
            } catch(Exception e) {e.printStackTrace();}
            return obj;
        }
        /// Create message from JSON Object
        public MessageHeader(JSONObject obj) throws JSONException {
                this.username = obj.getString("username");
                this.uuid = obj.getString("uuid");
                this.timestamp = obj.getString("timestamp");
                this.type = obj.getString("type");
        }
    }
}
