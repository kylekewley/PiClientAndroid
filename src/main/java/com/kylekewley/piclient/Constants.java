package com.kylekewley.piclient;

/**
 * Created by kylekewley on 7/13/14.
 */
public final class Constants {

    public enum ServerDefaultParserId {
        PING_ID(1),                 //Default parser.
        PARSE_ERROR_ID(2),          //Default parser.
        GROUP_REGISTRATION_ID(3);   //Default parser.

        private final int id;

        private ServerDefaultParserId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public enum ClientDefaultParserId {
        PARSE_ERROR_ID(2);      //Used to send an error message from the server.

        private final int id;

        private ClientDefaultParserId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

    }


}
