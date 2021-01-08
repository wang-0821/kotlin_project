import model.UnsafeInstanceObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.io.*;
import java.lang.reflect.Field;

/**
 * @author lix wang
 */
public class ObjectInstanceTest implements Serializable {
    @Test
    void testObjectClone() {
        CopyObject originObject = new CopyObject();
        originObject.val1 = 1;
        originObject.paramObject = new ParamObject();
        originObject.paramObject.val2 = 2;
        CopyObject copyObject = (CopyObject) originObject.clone();
        originObject.paramObject.val2 = 3;
        Assertions.assertEquals(copyObject.paramObject.val2, 3);
    }

    @Test
    void testObjectSerialize() {
        try {
            SerializeObject serializeObject = new SerializeObject();
            serializeObject.val3 = 3;
            serializeObject.paramObject = new SerializeParamObject();
            serializeObject.paramObject.val4 = 4;

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream("serialize_object.txt"));
            objectOutputStream.writeObject(serializeObject);

            ObjectInputStream objectInputStream = new ObjectInputStream(
                    new FileInputStream("serialize_object.txt"));
            SerializeObject deSerializedObject = (SerializeObject) objectInputStream.readObject();

            Assertions.assertNotSame(serializeObject, deSerializedObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testUnsafeAllocateInstance() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Unsafe unsafe = (Unsafe) field.get(null);

            UnsafeInstanceObject unsafeInstanceObject =
                    (UnsafeInstanceObject) unsafe.allocateInstance(UnsafeInstanceObject.class);
            Assertions.assertEquals(unsafeInstanceObject.val, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class CopyObject implements Cloneable {
        int val1;
        ParamObject paramObject;

        @Override
        protected Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private class ParamObject {
        int val2;
    }

    private class SerializeObject implements Serializable {
        int val3;
        SerializeParamObject paramObject;

        public SerializeObject() {
            System.out.println("Called constructor.");
        }
    }

    private class SerializeParamObject implements Serializable {
        int val4;
    }
}
