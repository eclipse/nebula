/*
 * Created on Apr 13, 2015
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer.example;

import java.security.MessageDigest;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.eclipse.nebula.widgets.xviewer.IXViewerPreComputedColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.example.model.SomeTask;

/**
 * @author Donald G. Dunne
 */
public class MyPreComputedColumn extends XViewerColumn implements IXViewerPreComputedColumn {

	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	public MyPreComputedColumn() {
		super(MyXViewerFactory.COLUMN_NAMESPACE + ".preComputedColumnExample", "Pre Computed Column", 130, XViewerAlign.Left, true, SortDataType.String, false, "Background loaded column that loads prior to setting inputs to XViewer.");
	}

	/**
	 * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
	 * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
	 */
	@Override
	public MyPreComputedColumn copy() {
		MyPreComputedColumn newXCol = new MyPreComputedColumn();
		super.copy(this, newXCol);
		return newXCol;
	}

	@Override
	public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {

		try {
			MessageDigest salt = MessageDigest.getInstance("SHA-256");
			for (Object obj : objects) {
				salt.update(UUID.randomUUID().toString().getBytes("UTF-8"));
				String digest = bytesToHex(salt.digest());
				preComputedValueMap.put(getKey(obj), "value " + digest);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

	@Override
	public Long getKey(Object obj) {
		return new Long(((SomeTask) obj).getId().hashCode());
	}

	@Override
	public String getText(Object obj, Long key, String cachedValue) {
		return cachedValue;
	}

}
