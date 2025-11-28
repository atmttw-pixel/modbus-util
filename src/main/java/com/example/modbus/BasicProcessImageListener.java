package com.example.modbus;

import com.serotonin.modbus4j.ProcessImageListener;

public class BasicProcessImageListener implements ProcessImageListener {
    @Override
    public void coilWrite(int i, boolean b, boolean b1) {

    }

    @Override
    public void holdingRegisterWrite(int offset, short oldValue, short newValue) {
//       System.out.println("Register at " + offset + "," + "change " + oldValue + " to " + newValue);
    }

}
