/*
 * This javascript accompanies the images/fileFormatIcons. The png icons are used to
 * display some content when the content is not mimtype image/???. Hence from the list
 * of all icons, using the file extension see if there is something that represents the
 * file. Otherwise a question mark will be used for thumbnails.
 */
var formatIconFilenameList = ["3dm.png", "3ds2.png", "3g22.png", "3gp2.png", "7z1.png", "aac.png", "aif.png", "ai.png",
  "apk.png", "app6.png", "asf1.png", "asp.png", "aspx.png", "asx2.png", "avi4.png",
  "bak.png", "bat8.png", "bin6.png", "bmp2.png", "cab.png", "cad1.png", "cdr2.png",
  "cer.png", "cfg2.png", "cfm2.png", "cgi2.png", "class3.png", "com.png", "cpl2.png",
  "cpp1.png", "crx.png", "csr.png", "css5.png", "csv2.png", "cue2.png", "cur1.png",
  "dat2.png", "db2.png", "dbf2.png", "dds2.png", "dem2.png", "dll2.png", "dmg3.png",
  "dmp2.png", "doc2.png", "docx.png", "drv1.png", "dtd2.png", "dwg2.png", "dxf1.png",
  "elf2.png", "eps2.png", "eps5.png", "exe3.png", "fileformats.png", "fla2.png",
  "flash10.png", "flv.png", "fnt.png", "fon1.png", "gam2.png", "gbr1.png", "ged2.png",
  "gif3.png", "gpx1.png", "gzip2.png", "gz.png", "hqz.png", "html9.png", "ibooks1.png",
  "icns.png", "ico2.png", "ics2.png", "iff.png", "indd2.png", "iso1.png", "iso4.png",
  "jar8.png", "jpg3.png", "js2.png", "jsp2.png", "key35.png", "kml1.png", "kmz.png",
  "lnk2.png", "log2.png", "lua.png", "m3u.png", "m4a1.png", "m4v2.png", "macho.png",
  "max2.png", "mdb2.png", "mdf2.png", "mid.png", "mim.png", "mov2.png", "mp36.png",
  "mp4.png", "mpa.png", "mpg3.png", "msg2.png", "msi2.png", "nes1.png", "object4.png",
  "odb2.png", "odc2.png", "odf2.png", "odg.png", "odi2.png", "odp2.png", "ods2.png",
  "odt5.png", "odt.png", "odx.png", "ogg.png", "otf1.png", "otf.png", "pages4.png",
  "pct2.png", "pdb2.png", "pdf19.png", "pif2.png", "pkg2.png", "pl2.png", "png3.png",
  "pps2.png", "ppt.png", "pptx2.png", "psd3.png", "ps.png", "pub2.png", "python3.png",
  "ra.png", "rar2.png", "raw2.png", "rm.png", "rom2.png", "rpm2.png", "rss29.png",
  "rtf.png", "sav.png", "sdf.png", "sitx2.png", "sql3.png", "sql5.png", "srt.png",
  "svg1.png", "swf1.png", "sys.png", "tar1.png", "tex.png", "tga2.png", "thm1.png",
  "tiff1.png", "tmp.png", "torrent.png", "ttf2.png", "txt2.png", "uue2.png",
  "vb.png", "vcd2.png", "vcf2.png", "vob.png", "wav2.png", "wma1.png", "wmv2.png",
  "wpd2.png", "wps.png", "wsf2.png", "xhtml.png", "xlr2.png", "xls1.png", "xlsx.png",
  "xml5.png", "yuv2.png", "zip6.png"];
var fileExtensions = ["3dm", "3ds", "3g2", "3gp", "7z1", "aac", "aif", "ai",
  "apk", "app", "asf", "asp", "aspx", "asx", "avi",
  "bak", "bat", "bin", "bmp", "cab", "cad", "cdr",
  "cer", "cfg", "cfm", "cgi", "class", "com", "cpl",
  "cpp", "crx", "csr", "css", "csv", "cue", "cur",
  "dat", "db", "dbf", "dds", "dem", "dll", "dmg",
  "dmp", "doc", "docx", "drv", "dtd", "dwg", "dxf",
  "elf", "eps", "eps", "exe", "fileformats", "fla",
  "flash", "flv", "fnt", "fon", "gam", "gbr", "ged",
  "gif", "gpx", "gzip", "gz", "hqz", "html", "ibooks",
  "icns", "ico", "ics", "iff", "indd", "iso", "iso4",
  "jar", "jpg", "js", "jsp", "key", "kml", "kmz",
  "lnk", "log", "lua", "m3u", "m4a", "m4v", "macho",
  "max", "mdb", "mdf", "mid", "mim", "mov", "mp3",
  "mp4", "mpa", "mpg", "msg", "msi", "nes", "object",
  "odb", "odc", "odf", "odg", "odi", "odp", "ods",
  "odt5", "odt", "odx", "ogg", "otf1", "otf", "pages",
  "pct", "pdb", "pdf", "pif", "pkg", "pl", "png",
  "pps", "ppt", "pptx", "psd", "ps", "pub", "python",
  "ra", "rar", "raw", "rm", "rom", "rpm", "rss",
  "rtf", "sav", "sdf", "sitx", "sql3", "sql", "srt",
  "svg", "swf", "sys", "tar", "tex", "tga", "thm",
  "tiff", "tmp", "torrent", "ttf", "txt", "uue",
  "vb", "vcd", "vcf", "vob", "wav", "wma", "wmv",
  "wpd", "wps", "wsf", "xhtml", "xlr", "xls", "xlsx",
  "xml", "yuv", "zip"];

// From the file extension determine what icon to display
function iconFileName(thizSrc, fileNameToTest) {

  // Must check for recursion!
  var currentSrc = thizSrc.src.toString().split("/").pop();
  if (currentSrc !== "ajax-loader.gif") {
    return;
  }

  var fileExtensionTotest = fileNameToTest.split('.').pop();
  var index = fileExtensions.indexOf(fileExtensionTotest);
  var filename = "";
  if (index === -1) {
    filename = "images/question-mark.png";
  } else {
    filename = "images/fileFormatIcons/png/" + formatIconFilenameList[index];
  }

  // Reassign the img to the new file that should be displayed
  thizSrc.src = filename;
  thizSrc.alt = filename;
}



