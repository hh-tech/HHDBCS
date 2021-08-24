package com.hh.hhdb_admin;

import com.hh.frame.common.base.BaseProduct;
import com.hh.frame.lang.LangUtil;
import com.hh.hhdb_admin.common.util.StartUtil;

import java.io.File;
import java.io.IOException;

public class CSProduct extends BaseProduct {

	public CSProduct() {
		this.version=StartUtil.CS_VERSION;
		this.pubKey=StartUtil.publicKey;
	}


	public static void main(String[] args) throws IOException {
		CSProduct p=new CSProduct();
		if(args[0].equals("ver")) {
			System.out.println(p.getVersion());
		}else {
			p.setCommitGitTag(args[0]);
			p.setCommitDate(args[1]);

			File verFile=new File(args[2],VERSION_FILE_NAME);
			BaseProduct frameP=CSProduct.readProductJson(verFile);
			p.addDepProduct(frameP);

			LangUtil.loadMerge(CSProduct.class);
			p.writeProductJson2Dir(new File("./hhdb_csadmin/etc"));
		}
	}

}
