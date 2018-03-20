package com.nowcoder.async;

public enum EventType {
    LIKE(0),COMMENT(1),LOGIN(2),MAIL(3);
    private int value;
    EventType(int value){
        this.value = value;
    }
    public int getValue(){
        return this.value;
    }
    public static void main(String[] args){
        System.out.println(LIKE.getClass());
    }
}
