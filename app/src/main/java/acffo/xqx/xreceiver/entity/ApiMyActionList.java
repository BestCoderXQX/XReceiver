package acffo.xqx.xreceiver.entity;

import java.util.List;

/**
 *  获取的动作列表mode
 *
 */
public class ApiMyActionList {
    /**
     * code : 0
     * data : {"move_count":0,"all_parts":["string"],"mac_address_list":["string"],"move_list":[{"custom_move_id":0,"set_count":0,"set_size":0,"mode_id":0,"completion_count":0,"direction":"string","pass_angle":0,"interval":0,"part_list":["string"],"range":"string","disp_type":"string","move_code":"string","hold_time":0}]}
     * msg : string
     */

    private int code;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * move_count : 0
         * all_parts : ["string"]
         * mac_address_list : ["string"]
         * move_list : [{"custom_move_id":0,"set_count":0,"set_size":0,"mode_id":0,"completion_count":0,"direction":"string","pass_angle":0,"interval":0,"part_list":["string"],"range":"string","disp_type":"string","move_code":"string","hold_time":0}]
         */

        private int move_count;
        private List<String> all_parts;
        private List<String> mac_address_list;
        private List<MoveListBean> move_list;

        public int getMove_count() {
            return move_count;
        }

        public void setMove_count(int move_count) {
            this.move_count = move_count;
        }

        public List<String> getAll_parts() {
            return all_parts;
        }

        public void setAll_parts(List<String> all_parts) {
            this.all_parts = all_parts;
        }

        public List<String> getMac_address_list() {
            return mac_address_list;
        }

        public void setMac_address_list(List<String> mac_address_list) {
            this.mac_address_list = mac_address_list;
        }

        public List<MoveListBean> getMove_list() {
            return move_list;
        }

        public void setMove_list(List<MoveListBean> move_list) {
            this.move_list = move_list;
        }

        public static class MoveListBean {
            /**
             * custom_move_id : 0
             * set_count : 0
             * set_size : 0
             * mode_id : 0
             * completion_count : 0
             * direction : string
             * pass_angle : 0
             * interval : 0
             * part_list : ["string"]
             * range : string
             * disp_type : string
             * move_code : string
             * hold_time : 0
             */

            private int custom_move_id;
            private int set_count;
            private int set_size;
            private int mode_id;
            private int completion_count;
            private String direction;
            private int pass_angle;
            private int interval;
            private String range;
            private String disp_type;
            private String move_code;
            private int hold_time;
            private List<String> part_list;

            public int getCustom_move_id() {
                return custom_move_id;
            }

            public void setCustom_move_id(int custom_move_id) {
                this.custom_move_id = custom_move_id;
            }

            public int getSet_count() {
                return set_count;
            }

            public void setSet_count(int set_count) {
                this.set_count = set_count;
            }

            public int getSet_size() {
                return set_size;
            }

            public void setSet_size(int set_size) {
                this.set_size = set_size;
            }

            public int getMode_id() {
                return mode_id;
            }

            public void setMode_id(int mode_id) {
                this.mode_id = mode_id;
            }

            public int getCompletion_count() {
                return completion_count;
            }

            public void setCompletion_count(int completion_count) {
                this.completion_count = completion_count;
            }

            public String getDirection() {
                return direction;
            }

            public void setDirection(String direction) {
                this.direction = direction;
            }

            public int getPass_angle() {
                return pass_angle;
            }

            public void setPass_angle(int pass_angle) {
                this.pass_angle = pass_angle;
            }

            public int getInterval() {
                return interval;
            }

            public void setInterval(int interval) {
                this.interval = interval;
            }

            public String getRange() {
                return range;
            }

            public void setRange(String range) {
                this.range = range;
            }

            public String getDisp_type() {
                return disp_type;
            }

            public void setDisp_type(String disp_type) {
                this.disp_type = disp_type;
            }

            public String getMove_code() {
                return move_code;
            }

            public void setMove_code(String move_code) {
                this.move_code = move_code;
            }

            public int getHold_time() {
                return hold_time;
            }

            public void setHold_time(int hold_time) {
                this.hold_time = hold_time;
            }

            public List<String> getPart_list() {
                return part_list;
            }

            public void setPart_list(List<String> part_list) {
                this.part_list = part_list;
            }
        }
    }
}
