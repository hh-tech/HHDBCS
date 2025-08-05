package com.hh.hhdb_admin;

import com.hh.frame.common.base.BaseProduct;
import com.hh.frame.lang2.LangMgr;
import com.hh.hhdb_admin.common.util.CsVarUtil;

import java.io.File;
import java.io.IOException;

public class CSProduct extends BaseProduct {
	static {
		try {
			LangMgr.loadProductLang(CSProduct.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public CSProduct() {
		this.version="7.8.X";
		this.pubKey=CsVarUtil.publicKey;
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

			p.writeProductJson2Dir(new File(args[3]));
			p.writeProductJson2Dir(new File("./etc"));
		}
	}

}
