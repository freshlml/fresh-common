package com.fresh.common.hsh;

public class EqsHshExpSub extends EqsHshExp {

    private int e;
    public EqsHshExpSub(int e, int code, boolean isPrimary, Integer wing, String name, Bean2Cool cool, int[] codes, Bean2Cool[] cools) {
        super(code, isPrimary, wing, name, cool, codes, cools);
        this.e = e;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o == null) return false;
        if(this.getClass() != o.getClass()) return false;
        EqsHshExpSub that = (EqsHshExpSub) o;
        boolean flag = this.e==that.e;
        flag &= super.equals(o);
        return flag;
    }

    @Override
    public int hashCode() {
        int hashCode = Integer.hashCode(this.e);
        hashCode = 29*hashCode + super.hashCode();
        return hashCode;
    }

    public static void main(String argv[]) {
        /** EqsHshExpSub.equals(EqsHshExpSub)
         * */
        EqsHshExpSub sub1 = new EqsHshExpSub(2, 1, true,
                1, "name", new Bean2Cool(1), new int[]{1}, new Bean2Cool[]{new Bean2Cool(2)});

        EqsHshExpSub sub2 = new EqsHshExpSub(2, 1, true,
                1, "name", new Bean2Cool(1), new int[]{1}, new Bean2Cool[]{new Bean2Cool(2)});
        System.out.println("sub1 == sub2: " + sub1.equals(sub2));

        EqsHshExp exp1 = new EqsHshExp(1, true,
                1, "name", new Bean2Cool(1), new int[]{1}, new Bean2Cool[]{new Bean2Cool(2)});
        System.out.println("sub1 == exp1: " + sub1.equals(exp1));

        System.out.println("sub1 hashCode: " + sub1.hashCode());
    }

}
