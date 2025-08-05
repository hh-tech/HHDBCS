package com.hh.hhdb_admin.mgr.multi_view.comp;

import com.hh.frame.mongo.obj.MongoDocUtil;
import com.hh.frame.swingui.view.tab.col.abs.AbsCol;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class DocCol extends AbsCol {
    public DocCol(String name, String value) {
        super(name, value);
    }

    @Override
    public TableCellEditor newColEditor() {
        return null;
    }

    @Override
    public TableCellRenderer newColRender() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, MongoDocUtil.getContent(value.toString()), isSelected, hasFocus, row, column);
            }
        };
    }
}
