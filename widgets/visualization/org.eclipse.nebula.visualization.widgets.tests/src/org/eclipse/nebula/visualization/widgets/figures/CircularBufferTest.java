/**
 * $Id: development_setup_admin_console.txt,v 1.2 2014/10/14 02:03:19 ken Exp $
 * 
 * (c) Copyright, Real-Time Innovations, $Date: 2014/10/14 02:03:19 $.
 * All rights reserved.
 * 
 * No duplications, whole or partial, manual or electronic, may be made
 * without express written permission. Any such copies, or
 * revisions thereof, must display this notice unaltered.
 * This code contains trade secrets of Real-Time Innovations, Inc.
 * 
 * modification history:
 * ---------------------
 * 
 * ===========================================================================
 */

package org.eclipse.nebula.visualization.widgets.figures;

import static org.junit.Assert.assertEquals;

import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBuffer;
import org.junit.Test;

/**JUnit test for circular buffer.
 * @author Xihui Chen
 *
 */
public class CircularBufferTest {

	@Test
	public void test() {

		int[] data = new int[] { 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 };
		CircularBuffer<Integer> buffer = new CircularBuffer<Integer>(50);
		for (int i = 0; i < data.length; i++) {
			buffer.add(data[i]);
		}
		int i = 0;
		for (int v : buffer) {
			assertEquals(data[i++], v);
		}

		buffer = new CircularBuffer<Integer>(7);
		for (i = 0; i < data.length; i++) {
			buffer.add(data[i]);
		}
		i = 3;
		for (int v : buffer) {
			assertEquals(data[i++], v);
		}

		// test increase buffer size
		buffer.setBufferSize(100, false);
		i = 3;
		for (int v : buffer) {
			assertEquals(data[i++], v);
		}
		
		// test decrease buffer size
		buffer.setBufferSize(5, false);
		i = 5;
		for (int v : buffer) {
			assertEquals(data[i++], v);
		}

	}

}
