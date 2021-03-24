package com.oman.sayakil.model;

public class MemberModel {
    private String daysMember;
    private String memberDesc;
    private String memberPrice;

    public MemberModel(String daysMember, String memberDesc, String memberPrice) {
        this.daysMember = daysMember;
        this.memberDesc = memberDesc;
        this.memberPrice = memberPrice;
    }

    public String getDaysMember() {
        return daysMember;
    }

    public void setDaysMember(String daysMember) {
        this.daysMember = daysMember;
    }

    public String getMemberDesc() {
        return memberDesc;
    }

    public void setMemberDesc(String memberDesc) {
        this.memberDesc = memberDesc;
    }

    public String getMemberPrice() {
        return memberPrice;
    }

    public void setMemberPrice(String memberPrice) {
        this.memberPrice = memberPrice;
    }
}
