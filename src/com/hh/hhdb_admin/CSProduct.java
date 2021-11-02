package com.hh.hhdb_admin;

import com.hh.frame.common.base.BaseProduct;
import com.hh.frame.lang.LangMgr2;
import com.hh.hhdb_admin.common.util.StartUtil;

import java.io.File;
import java.io.IOException;

public class CSProduct extends BaseProduct {

	static {
		try {
			LangMgr2.loadMerge(CSProduct.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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

			p.writeProductJson2Dir(new File("./hhdb_csadmin/etc"));
			p.writeProductJson2Dir(new File("./etc"));
		}
	}

}
