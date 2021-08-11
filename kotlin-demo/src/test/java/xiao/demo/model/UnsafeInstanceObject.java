package xiao.demo.model;

/**
 * @author lix wang
 */
public class UnsafeInstanceObject {
    public int val =  2;

    public UnsafeInstanceObject(int val) {
        this.val = val;
    }

    private UnsafeInstanceObject() {
        System.out.println("Called " + this.getClass().getSimpleName() + " constructor.");
    }
}
