package util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class LimitedDocument extends PlainDocument {
    private final int maxLength;

    public LimitedDocument(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null) return;
        if ((getLength() + str.length()) <= maxLength) {
            super.insertString(offset, str, attr);
        } else {
            int allowedLength = maxLength - getLength();
            if (allowedLength > 0) {
                super.insertString(offset, str.substring(0, allowedLength), attr);
            }
        }
    }
}
