package xxx.joker.apps.encryptor;

import xxx.joker.libs.javalibs.utils.JkConsole;
import xxx.joker.libs.javalibs.utils.JkEncryption;

import javax.crypto.BadPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

/**
 * Created by f.barbano on 22/07/2017.
 */
public class Main {
	private static final String EXT_ENCR = ".encr";
	private static final String EXT_DECR = ".decr";

	public static void main(String[] args) throws GeneralSecurityException, IOException {
		if(args.length < 2 || args.length > 4) {
			String mex = "USAGE:  encr  [-d]  <FILE_TO_ENCRYPT>  <PASSWORD>  [-new]\n";
			mex += "\t-d: decrypt\n";
			mex += "\t-new: new file, not overridden";
			exit(mex);
		}

		boolean encrypt = !args[0].equals("-d");
		int argPos = encrypt ? 0 : 1;

		Path inputPath = Paths.get(args[argPos]);
		if(!Files.exists(inputPath)) {
			exit("File [" + inputPath + "] not found");
		} else if(!Files.isRegularFile(inputPath)) {
			exit("The path [" + inputPath + "] is not a regular file");
		}

		argPos++;
		Path input = inputPath.toAbsolutePath().normalize();
		String pwd = JkEncryption.getMD5(args[argPos]);
		Path outPath = Paths.get(input.toString() + (encrypt ? EXT_ENCR : EXT_DECR));

		argPos++;
		boolean override = true;
		if(argPos < args.length) {
			if(args[argPos].equals("-new")) {
				override = false;
			}
		}
		// encrypt/decrypt the file
		if(encrypt) {
			JkEncryption.encryptFile(input, outPath, pwd, true);
		} else {
			try {
				JkEncryption.decryptFile(input, outPath, pwd, true);
			} catch(BadPaddingException ex) {
				exit("Wrong password!");
			}
		}

		if(override) {
			Files.delete(input);
			Files.move(outPath, input);
		}

	}

	private static void exit(String mex) {
		JkConsole.display(mex);
		System.exit(1);
	}

}
