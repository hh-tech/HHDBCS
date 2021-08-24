package com.hh.hhdb_admin.test.trigger;

import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.mgr.trigger.comp.TriggerEditDialog;

public class TriggerEditDialogTest {
    public static void main(String[] args)  {
        try {
            HHSwingUi.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        TriggerEditDialog dialog = new TriggerEditDialog("CREATE OR REPLACE FUNCTION auditlogfunc() RETURNS TRIGGER AS $example_table$\n" +
                "   BEGIN\n" +
                "      INSERT INTO AUDIT(EMP_ID, ENTRY_DATE) VALUES (new.ID, current_timestamp);\n" +
                "      RETURN NEW;\n" +
                "   END;\n" +
                "$example_table$ LANGUAGE plpgsql;");
        dialog.getToolBar().add(new HButton("测试按钮1"));
        dialog.show();
    }
}
