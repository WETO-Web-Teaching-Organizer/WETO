package fi.uta.cs.weto.model;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.weto.db.Document;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class DocumentModel
{
  public final static int bufferSize = 8192;

  public static void loadDocument(Connection conn, Document doc, File target)
          throws SQLException, FileNotFoundException, IOException
  {
    byte[] buffer = new byte[bufferSize];
    final boolean commitMode = conn.getAutoCommit();
    try
    {
      conn.setAutoCommit(false);
      LargeObjectManager lobMan = ((org.postgresql.PGConnection) conn)
              .getLargeObjectAPI();
      LargeObject lob = lobMan.open((long) doc.getContentId(),
              LargeObjectManager.READ);
      try(OutputStream out = new BufferedOutputStream(new FileOutputStream(
              target)))
      {
        if(doc.getContentMimeType().endsWith("zip"))
        {
          int bytesRead = 0;
          while((bytesRead = lob.read(buffer, 0, bufferSize)) > 0)
          {
            out.write(buffer, 0, bytesRead);
          }
        }
        else
        {
          try(ZipInputStream zip = new ZipInputStream(
                  new BufferedInputStream(lob.getInputStream())))
          {
            zip.getNextEntry();
            int bytesRead = 0;
            while((bytesRead = zip.read(buffer)) != -1)
            {
              out.write(buffer, 0, bytesRead);
            }
          }
        }
      }
      finally
      {
        lob.close();
      }
    }
    finally
    {
      conn.setAutoCommit(commitMode);
    }
  }

  public static InputStream getDocumentInputStream(Connection conn, Document doc)
          throws SQLException
  {
    long oid = doc.getContentId();
    LargeObjectManager lobMan = ((org.postgresql.PGConnection) conn)
            .getLargeObjectAPI();
    LargeObject lob = lobMan.open(oid, LargeObjectManager.READ);
    return lob.getInputStream();
  }

  public static String readTextDocument(Connection conn, Document doc)
          throws SQLException, IOException
  {
    char[] buffer = new char[bufferSize / 2];
    final boolean commitMode = conn.getAutoCommit();
    String documentText = null;
    try
    {
      conn.setAutoCommit(false);
      long oid = doc.getContentId();
      LargeObjectManager lobMan = ((org.postgresql.PGConnection) conn)
              .getLargeObjectAPI();
      LargeObject lob = lobMan.open(oid, LargeObjectManager.READ);
      StringWriter textBuffer = new StringWriter();
      InputStream is = lob.getInputStream();
      if(doc.getMimeType().endsWith("zip"))
      {
        ZipInputStream zis = new ZipInputStream(is);
        zis.getNextEntry();
        is = zis;
      }
      try(BufferedReader br = new BufferedReader(new InputStreamReader(is,
              "UTF-8")))
      {
        int charsRead = 0;
        while((charsRead = br.read(buffer)) != -1)
        {
          textBuffer.write(buffer, 0, charsRead);
        }
        documentText = textBuffer.toString();
      }
    }
    finally
    {
      conn.setAutoCommit(commitMode);
    }
    return documentText;
  }

  public static void updateTextDocument(Connection conn, Document doc,
          String text)
          throws InvalidValueException, SQLException, IOException,
                 ObjectNotValidException, NoSuchItemException
  {
    int fileSize = 0;
    int dbFileSize;
    final boolean commitMode = conn.getAutoCommit();
    try
    {
      conn.setAutoCommit(false);
      LargeObjectManager lobMan = ((org.postgresql.PGConnection) conn)
              .getLargeObjectAPI();
      long oid = doc.getContentId();
      LargeObject lob = lobMan.open(oid, LargeObjectManager.WRITE);
      lob.truncate(0);
      try(InputStream stringStream = new ByteArrayInputStream(text.getBytes(
              "UTF-8")))
      {
        byte[] buffer = new byte[bufferSize];
        doc.setMimeType("application/zip");
        try(ZipOutputStream zipStream = new ZipOutputStream(
                new BufferedOutputStream(lob.getOutputStream())))
        {
          ZipEntry theEntry = new ZipEntry(doc.getFileName());
          zipStream.putNextEntry(theEntry);
          int byteCount = 0;
          while((byteCount = stringStream.read(buffer)) != -1)
          {
            zipStream.write(buffer, 0, byteCount);
            fileSize += byteCount;
          }
          zipStream.closeEntry();
          dbFileSize = (int) theEntry.getCompressedSize();
        }
      }
      finally
      {
        lob.close();
      }
      doc.setFileSize(dbFileSize);
      doc.setMimeType("application/zip");
      doc.setContentFileSize(fileSize);
      doc.update(conn);
    }
    finally
    {
      conn.setAutoCommit(commitMode);
    }
  }

  public static Document storeDocument(Connection conn, File file,
          String fileName)
          throws InvalidValueException, SQLException, IOException,
                 ObjectNotValidException, NoSuchItemException
  {
    return replaceDocument(conn, file, fileName, null);
  }

  public static Document replaceDocument(Connection conn, File file,
          String fileName, Document document)
          throws InvalidValueException, SQLException, IOException,
                 ObjectNotValidException, NoSuchItemException
  {
    String dbContentType;
    String origContentType;
    ContentHandler contenthandler = new BodyContentHandler();
    Metadata metadata = new Metadata();
    metadata.set(Metadata.RESOURCE_NAME_KEY, fileName);
    AutoDetectParser parser = new AutoDetectParser();
    try(InputStream fileStream = new BufferedInputStream(new FileInputStream(
            file)))
    {
      parser.parse(fileStream, contenthandler, metadata);
    }
    catch(SAXException | TikaException e)
    {
    }
    origContentType = metadata.get(Metadata.CONTENT_TYPE);
    dbContentType = origContentType;
    int fileSize = 0;
    int dbFileSize;
    final boolean commitMode = conn.getAutoCommit();
    try
    {
      conn.setAutoCommit(false);
      LargeObjectManager lobMan = ((org.postgresql.PGConnection) conn)
              .getLargeObjectAPI();
      long oid = (document == null) ? lobMan.createLO() : document
              .getContentId();
      LargeObject lob = lobMan.open(oid, LargeObjectManager.WRITE);
      lob.truncate(0);
      try(InputStream fileStream = new BufferedInputStream(new FileInputStream(
              file)))
      {
        byte[] buffer = new byte[bufferSize];
        if(origContentType.endsWith("zip"))
        {
          int bytesRead;
          while((bytesRead = fileStream.read(buffer)) > -1)
          {
            lob.write(buffer, 0, bytesRead);
            fileSize += bytesRead;
          }
          dbFileSize = fileSize;
        }
        else
        {
          dbContentType = "application/zip";
          try(ZipOutputStream zipStream = new ZipOutputStream(
                  new BufferedOutputStream(lob.getOutputStream())))
          {
            ZipEntry theEntry = new ZipEntry(fileName);
            zipStream.putNextEntry(theEntry);
            int byteCount = 0;
            while((byteCount = fileStream.read(buffer)) != -1)
            {
              zipStream.write(buffer, 0, byteCount);
              fileSize += byteCount;
            }
            zipStream.closeEntry();
            dbFileSize = (int) theEntry.getCompressedSize();
          }
        }
      }
      finally
      {
        lob.close();
      }
      boolean doUpdate = true;
      if(document == null)
      {
        document = new Document();
        doUpdate = false;
      }
      document.setFileName(fileName.replace(",", "_"));
      document.setFileSize(dbFileSize);
      document.setMimeType(dbContentType);
      document.setContentFileSize(fileSize);
      document.setContentMimeType(origContentType);
      document.setContentId(new Long(oid).intValue());
      if(doUpdate)
      {
        document.update(conn);
      }
      else
      {
        document.insert(conn);
      }
    }
    finally
    {
      conn.setAutoCommit(commitMode);
    }
    return document;
  }

  public static void deleteDocument(Connection conn, Document document)
          throws SQLException, TooManyItemsException, NoSuchItemException
  {
    long oid = document.getContentId();
    LargeObjectManager lobMan = ((org.postgresql.PGConnection) conn)
            .getLargeObjectAPI();
    lobMan.delete(oid);
    document.delete(conn);
  }

}
