package com.comcast.csv.zucchini

class Veggie {

    boolean clean = false
    boolean cooked = false
    
    String getTaste() {
        return clean ? cooked ? 'delicious' : 'crunchy' : 'gross'
    }
}
