package nl.tudelft.cs4160.trustchain_android;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import nl.tudelft.cs4160.trustchain_android.block.ValidationResult;

import static org.junit.Assert.assertEquals;

/**
 * Created by Boning on 12/16/2017.
 */

public class ValidationResultTest {

    @Test
    public void testConstructor() {
        ValidationResult vr = new ValidationResult();
        assertEquals(vr.getStatus(), ValidationResult.VALID);
        assertEquals(vr.toString(), "<ValidationResult: VALID>");
    }

    @Test
    public void testPartial() {
        ValidationResult vr = new ValidationResult();
        vr.setPartial();
        assertEquals(vr.getStatus(), ValidationResult.PARTIAL);
        assertEquals(vr.toString(), "<ValidationResult: PARTIAL>");
    }

    @Test
    public void testPartialNext() {
        ValidationResult vr = new ValidationResult();
        vr.setPartialNext();
        assertEquals(vr.getStatus(), ValidationResult.PARTIAL_NEXT);
        assertEquals(vr.toString(), "<ValidationResult: PARTIAL_NEXT>");
    }

    @Test
    public void testPartialPrevious() {
        ValidationResult vr = new ValidationResult();
        vr.setPartialPrevious();
        assertEquals(vr.getStatus(), ValidationResult.PARTIAL_PREVIOUS);
        assertEquals(vr.toString(), "<ValidationResult: PARTIAL_PREVIOUS>");
    }

    @Test
    public void testNoInfo() {
        ValidationResult vr = new ValidationResult();
        vr.setNoInfo();
        assertEquals(vr.getStatus(), ValidationResult.NO_INFO);
        assertEquals(vr.toString(), "<ValidationResult: NO_INFO>");
    }

    @Test
    public void testInvalid() {
        ValidationResult vr = new ValidationResult();
        vr.setInvalid();
        assertEquals(vr.getStatus(), ValidationResult.INVALID);
        assertEquals(vr.toString(), "<ValidationResult: INVALID>");
    }

    @Test
    public void testGetterSetter() {
        ValidationResult vr = new ValidationResult();
        vr.setStatus(3);
        assertEquals(vr.getStatus(), ValidationResult.PARTIAL_PREVIOUS);
        vr.setStatus(4);
        assertEquals(vr.getStatus(), ValidationResult.NO_INFO);
    }

    @Test
    public void testSetErrors() {
        ValidationResult vr = new ValidationResult();
        List<String> errors = new ArrayList<>();
        String test = "test";
        errors.add(test);
        vr.setErrors(errors);
        assertEquals(vr.getErrors().get(0), test);
    }
}