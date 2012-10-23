package org.eclipse.nebula.widgets.oscilloscope;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;
import junit.framework.Assert;

import org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope.IntegerFiFoCircularStack;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class OscilloscopeTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void stackTestEmpty() {

		stackTestEmptyInternal(2);
		stackTestEmptyInternal(3);
		stackTestEmptyInternal(100);

	}

	@Test
	public void zeroSizeStackTest() {

		Oscilloscope x = new Oscilloscope(new Shell(), 0);

		try {
			IntegerFiFoCircularStack stack = x.new IntegerFiFoCircularStack(0);
		} catch (Exception e) {
			return;
		}

		Assert.fail("should not be here");

	}

	@Test
	public void oneSizeStackTest() {

		Oscilloscope x = new Oscilloscope(new Shell(), 0);

		try {
			IntegerFiFoCircularStack stack = x.new IntegerFiFoCircularStack(1);
		} catch (Exception e) {
			return;
		}

		Assert.fail("should not be here");

	}

	@Test
	public void negativeSizeStackTest() {

		Oscilloscope x = new Oscilloscope(new Shell(), 0);

		try {
			IntegerFiFoCircularStack stack = x.new IntegerFiFoCircularStack(-10);
		} catch (Exception e) {
			return;
		}

		Assert.fail("should not be here");

	}

	@Test
	public void copyStackTest() {

		Oscilloscope x = new Oscilloscope(new Shell(), 0);
		IntegerFiFoCircularStack stack = x.new IntegerFiFoCircularStack(10);

		for (int i = 0; i < 7; i++) {
			stack.push(i);
		}
		assertTrue(stack.getLoad() == 7);

		IntegerFiFoCircularStack stack2 = x.new IntegerFiFoCircularStack(5,
				stack);
		assertTrue(stack2.getLoad() == 5);

		for (int i = 0; i < 7; i++) {
			stack.push(i);
		}
		stack2 = x.new IntegerFiFoCircularStack(7, stack);
		assertTrue(stack2.getLoad() + "", stack2.getLoad() == 7);
		assertTrue(stack2.isFull());
		assertTrue(stack.isEmpty());

		for (int i = 0; i < 7; i++) {
			stack.push(i);
		}

		assertTrue(stack2.pop(3) == stack.pop(4));
		assertTrue(stack2.pop(3) == stack.pop(4));
		assertTrue(stack2.pop(3) == stack.pop(4));
		assertTrue(stack2.pop(3) == stack.pop(4));
		assertTrue(stack2.pop(3) == stack.pop(4));
		assertTrue(stack2.pop(3) == stack.pop(4));
		assertTrue(stack2.pop(3) == stack.pop(4));
		assertFalse(stack2.pop(3) == stack.pop(4));

		assertTrue(stack2.getLoad() == 0);
		assertTrue(stack2.isEmpty());
		assertTrue(stack.isEmpty());

	}

	private void stackTestEmptyInternal(int capacity) {
		Oscilloscope x = new Oscilloscope(new Shell(), 0);
		IntegerFiFoCircularStack stack = x.new IntegerFiFoCircularStack(
				capacity);
		assertTrue(stack.isEmpty());

		stack.push(5);
		assertFalse(stack.isEmpty());

		stack.peek(0);
		assertFalse(stack.isEmpty());

		stack.pop(0);
		assertTrue(stack.isEmpty());

		for (int i = 0; i < capacity; i++) {
			stack.push(i);
		}

		assertTrue(stack.isFull());
		assertFalse(stack.isEmpty());

		for (int i = 0; i < capacity; i++) {
			assertFalse(stack.isEmpty());
			assertTrue(stack.pop(-1) != -1);
			assertFalse(stack.isFull());
		}

		assertTrue(stack.isEmpty());

	}

}
