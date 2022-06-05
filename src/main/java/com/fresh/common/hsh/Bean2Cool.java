package com.fresh.common.hsh;

public class Bean2Cool {
    private int i;

    public Bean2Cool(int i) {
        this.i = i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o == null) return false;
        if(this.getClass() != o.getClass()) return false;
        Bean2Cool that = (Bean2Cool) o;
        boolean flag = this.i==that.i;
        return flag;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(i);
    }

}
