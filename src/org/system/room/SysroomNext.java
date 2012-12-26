/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.system.room;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.ExifThumbnailDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 *
 * @author lisa
 */
public class SysroomNext {

    /**
     *
     * @param s
     * @return
     */
    public static String getFileName(String s) {
        return null;

    }

    /**
     * 古いファイルを削除する
     *
     * @param fld
     * @param days
     */
    public static void deteteOldFile(String fld, int days) {
        File[] dltfiles = getOldFiles(fld, days);
        for (File dlt : dltfiles) {
            if (dlt.exists()) {
                dlt.delete();
            }
        }
    }

    /**
     *
     * @param fld folder
     * @param days マイナスで指定(何日前）
     * @return 日付の古いファイルを返す
     */
    public static File[] getOldFiles(String fld, int days) {
        Date t;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN);
        cal.set(Calendar.HOUR_OF_DAY, 0); //am 0:0:1にする
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 58);
        t = cal.getTime();
        String format;
        format = DateFormatUtils.format(t, DateFormatUtils.ISO_DATETIME_FORMAT.getPattern());
        //System.out.println("" + format);
        Date old = DateUtils.addDays(t, days);
        Calendar oldCal = DateUtils.toCalendar(old);
        oldCal.set(Calendar.HOUR_OF_DAY, 0); //am 0:0:1にする
        oldCal.set(Calendar.MINUTE, 59);
        oldCal.set(Calendar.SECOND, 58);

        //oldCal.set(Calendar.YEAR, old.);

        format = DateFormatUtils.format(oldCal.getTime(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern());
        // System.out.println("" + format);

        File dir = new File(fld);
        FileFilter ageFileFilter = FileFilterUtils.ageFileFilter(oldCal.getTime());
        File[] listFiles = dir.listFiles(ageFileFilter);
        return listFiles;
    }

    /**
     * sorted array jepg files
     *
     * @param fld Folder name
     * @return newDate ~ oldDate
     */
    public static File[] getJpgFilesReversSortAaary(String fld) {
        File dir = new File(fld);
        File[] f;
        FileFilter and = FileFilterUtils.and(
                FileFilterUtils.notFileFilter(
                FileFilterUtils.directoryFileFilter()),
                FileFilterUtils.or(
                FileFilterUtils.suffixFileFilter(".jpg"),
                FileFilterUtils.suffixFileFilter(".JPG"),
                FileFilterUtils.suffixFileFilter(".JPEG"),
                FileFilterUtils.suffixFileFilter(".jpeg")));
        f = dir.listFiles(and);

        Arrays.sort(f, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        return f;

    }

    /**
     * jpg,jpegのファイル名のリストを返す
     *
     * @param fld
     * @return String[]
     */
    public static String[] getJpgFileNemes(String fld) {
        System.out.println("fld " + fld);
        File dir = new File(fld);
        String[] files = dir.list(
                FileFilterUtils.and(
                FileFilterUtils.notFileFilter(
                FileFilterUtils.directoryFileFilter()),
                FileFilterUtils.or(
                FileFilterUtils.suffixFileFilter(".jpg"),
                FileFilterUtils.suffixFileFilter(".JPG"),
                FileFilterUtils.suffixFileFilter(".JPEG"),
                FileFilterUtils.suffixFileFilter(".jpeg"))));
        return files;
    }

    public static File[] getJpgFiles(String fld) {
        File dir = new File(fld);

        FileFilter and = FileFilterUtils.and(
                FileFilterUtils.notFileFilter(
                FileFilterUtils.directoryFileFilter()),
                FileFilterUtils.or(
                FileFilterUtils.suffixFileFilter(".jpg"),
                FileFilterUtils.suffixFileFilter(".JPG"),
                FileFilterUtils.suffixFileFilter(".JPEG"),
                FileFilterUtils.suffixFileFilter(".jpeg")));
        return dir.listFiles(and);

    }

    public static BufferedImage getNotExifThumb(String src) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(src));
            BufferedImage thumbnail = Thumbnails.of(originalImage)
                    .scale(0.25f)
                    .asBufferedImage();

            return thumbnail;



        } catch (IOException ex) {
            Logger.getLogger(SysroomNext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public static Date hasExifDate(String src) {
        File jpegFile = new File(src);
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
            ExifSubIFDDirectory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
            if (directory != null) {
                Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                if (date != null) {
                    return date;
                }
            }

            ExifIFD0Directory directory2 = metadata.getDirectory(ExifIFD0Directory.class);
            if (directory2 != null) {
                Date date2 = directory2.getDate(ExifIFD0Directory.TAG_DATETIME);
                if (date2 != null) {
                    return date2;
                }

            }
        } catch (ImageProcessingException ex) {
            Logger.getLogger(SysroomNext.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SysroomNext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static byte[] hasExifThumb(String src) throws ImageProcessingException {
        byte[] thumbnailData = null;
        File jpegFile = new File(src);
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
            ExifThumbnailDirectory directory = (ExifThumbnailDirectory) metadata.getDirectory(ExifThumbnailDirectory.class);
            if (directory != null) {
                boolean hasThumbnailData = directory.hasThumbnailData();
                if (hasThumbnailData) {
                    //  System.out.println("hasThumbnailData " + hasThumbnailData);
                    thumbnailData = directory.getThumbnailData();


                }
            }


        } catch (IOException ex) {
            Logger.getLogger(SysroomNext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return thumbnailData;
    }

    public static Dimension hasJpegSize(String src) throws MetadataException {

        File jpegFile = new File(src);
        try {
            Metadata metadata = JpegMetadataReader.readMetadata(jpegFile);

            JpegDirectory directory = metadata.getDirectory(JpegDirectory.class);
            int imageHeight = directory.getImageHeight();
            int imageWidth = directory.getImageWidth();
            Dimension d = new Dimension(imageWidth, imageHeight);
            return d;
        } catch (ImageProcessingException ex) {
            Logger.getLogger(SysroomNext.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SysroomNext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Date getLastModified(String src) {
        File jpegFile = new File(src);


        //System.out.println("src " + src +  " [ " + jpegFile.lastModified()  + " ]");
        return new Date(jpegFile.lastModified());
    }

    public static void hasExif(String src) throws ImageProcessingException {
        boolean has = false;
        byte[] thumbnailData = null;
        File jpegFile = new File(src);
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
            ExifThumbnailDirectory directory = (ExifThumbnailDirectory) metadata.getDirectory(ExifThumbnailDirectory.class);
            if (directory != null) {
                boolean hasThumbnailData = directory.hasThumbnailData();
                if (hasThumbnailData) {
                    //System.out.println("hasThumbnailData " + hasThumbnailData);
                    thumbnailData = directory.getThumbnailData();
                    //System.out.println(src +"" + thumbnailData.length);

                }
            }


        } catch (IOException ex) {
            Logger.getLogger(SysroomNext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static GeoLocation hasGps(String src) throws ImageProcessingException {
        GeoLocation geoLocation = null;

        File jpegFile = new File(src);
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
            GpsDirectory directory = (GpsDirectory) metadata.getDirectory(GpsDirectory.class);
            if (directory != null) {
                geoLocation = directory.getGeoLocation();
                if (geoLocation != null) {


                    return geoLocation;
                }
            }


        } catch (IOException ex) {
            Logger.getLogger(SysroomNext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return geoLocation;
    }
    // this 2012.12.26 last row (302)
}
