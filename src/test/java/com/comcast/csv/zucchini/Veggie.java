package com.comcast.csv.zucchini;

public class Veggie {
    public boolean clean;
    public boolean cooked;

    public String getTaste() {
        if(!this.clean) {
            return "gross";
        }
        else {
            if(!this.cooked) {
                return "crunchy";
            }
            else {
                return "delicious";
            }
        }
    }
}
