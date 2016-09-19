package com.example.jeonghun.heathcare;

/**
 * Created by JeongHun on 16. 5. 22..
 */
public class PushEvent
{
    // the pushlist object being sent using the bus
    public final String data;

    public PushEvent(String data)
    {
        this.data = data;
    }

    /**
     * @return the pushlist
     */
    public String getData(){
        return data;
    }
}
