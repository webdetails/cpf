package pt.webdetails.cpf.impl;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import pt.webdetails.cpf.repository.IRepositoryFile;
import pt.webdetails.cpf.repository.IRepositoryFileFilter;

public class DefaultRepositoryFile implements IRepositoryFile {

	private File file;

	public DefaultRepositoryFile(File file) {
		this.file = file;
	}
	@Override
	public boolean isDirectory() {
		return file.isDirectory();
	}

	@Override
	public String getFileName() {
		return file.getName();
	}

	@Override
	public String getSolutionPath() {
		return file.getAbsolutePath();
	}

	@Override
	public String getSolution() {
		return file.getPath();
	}

	@Override
	public String getFullPath() {
		return file.getAbsolutePath();
	}

	@Override
	public IRepositoryFile[] listFiles() {
		return new IRepositoryFile[0];
	}

	@Override
	public IRepositoryFile[] listFiles(IRepositoryFileFilter iff) {
		return new IRepositoryFile[0];
	}

	@Override
	public boolean isRoot() {
		return false;
	}

	@Override
	public IRepositoryFile retrieveParent() {
		return new DefaultRepositoryFile(file.getParentFile());
	}

	@Override
	public byte[] getData() {
		try {
			if (file != null && file.exists() && file.canRead()) {
				return FileUtils.readFileToByteArray(file);
			}
		} catch (Exception e) {
			e.printStackTrace(); // XXX - we should throw an exception or log at least?
		}
		return new byte[0];
	}

	@Override
	public boolean exists() {
		return file.exists();
	}

	@Override
	public long getLastModified() {
		return file.lastModified();
	}

	@Override
	public String getExtension() {
		return FilenameUtils.getExtension(file.getName());
	}

}
