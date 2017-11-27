package com.example.nhan.hackathonmusicfinal.utils;

import android.content.Context;
import android.net.Uri;
import android.support.test.espresso.core.deps.guava.collect.Sets;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.nhan.hackathonmusicfinal.R;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.upstream.FileDataSource;
import com.google.android.exoplayer.util.Util;

import org.apache.commons.lang3.StringUtils;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * Created by Nhan on 10/31/2016.
 */

public class Utils {
    private static ExoPlayer exoPlayer = ExoPlayer.Factory.newInstance(1);
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;

    public static void setDataStreamExoPlayer(Context context, String url) {
        Uri radioUri = Uri.parse(url);

        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        String userAgent = Util.getUserAgent(context, "ExoPlayerDemo");
        DataSource dataSource = new DefaultUriDataSource(context, null, userAgent);
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                radioUri, dataSource, allocator, BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);

        exoPlayer.prepare(audioRenderer);
    }

    public static void setDataOfflineExoPlayer(String filePath){
        Uri radioUri = Uri.parse(filePath);
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        DataSource dataSource = new FileDataSource();
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                radioUri, dataSource, allocator, BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);

        exoPlayer.prepare(audioRenderer);
    }

    public static ExoPlayer getExoPlayer() {
        return exoPlayer;
    }
    public static void openFragment(FragmentManager fragmentManager, Fragment fragment){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }



    public static int getRatio(String s1, String s2, boolean debug) {

        if (s1.length() >= s2.length()) {
            // We need to swap s1 and s2
            String temp = s2;
            s2 = s1;
            s1 = temp;
        }

        // Get alpha numeric characters

        s1 = escapeString(s1);
        s2 = escapeString(s2);

        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();


        Set<String> set1 = new HashSet<String>();
        Set<String> set2 = new HashSet<String>();

        //split the string by space and store words in sets
        StringTokenizer st1 = new StringTokenizer(s1);
        while (st1.hasMoreTokens()) {
            set1.add(st1.nextToken());
        }

        StringTokenizer st2 = new StringTokenizer(s2);
        while (st2.hasMoreTokens()) {
            set2.add(st2.nextToken());
        }

        Sets.SetView<String> intersection = Sets.intersection(set1, set2);

        TreeSet<String> sortedIntersection = Sets.newTreeSet(intersection);

        if (debug) {
            System.out.print("Sorted intersection --> ");
            for (String s : sortedIntersection)
                System.out.print(s + " ");
        }

        // Find out difference of sets set1 and intersection of set1,set2

        Sets.SetView<String> restOfSet1 = Sets.symmetricDifference(set1, intersection);

        // Sort it

        TreeSet<String> sortedRestOfSet1 = Sets.newTreeSet(restOfSet1);

        Sets.SetView<String> restOfSet2 = Sets.symmetricDifference(set2, intersection);
        TreeSet<String> sortedRestOfSet2 = Sets.newTreeSet(restOfSet2);

        if (debug) {
            System.out.print("\nSorted rest of 1 --> ");
            for (String s : sortedRestOfSet1)
                System.out.print(s + " ");

            System.out.print("\nSorted rest of 2 -->");
            for (String s : sortedRestOfSet2)
                System.out.print(s + " ");
        }

        String t0 = "";
        String t1 = "";
        String t2 = "";

        for (String s : sortedIntersection) {
            t0 = t0 + " " + s;
        }
        t0 = t0.trim();

        Set<String> setT1 = Sets.union(sortedIntersection, sortedRestOfSet1);
        for (String s : setT1) {
            t1 = t1 + " " + s;
        }
        t1 = t1.trim();

        Set<String> setT2 = Sets.union(intersection, sortedRestOfSet2);
        for (String s : setT2) {
            t2 = t2 + " " + s;
        }

        t2 = t2.trim();


        int amt1 = calculateLevensteinDistance(t0, t1);
        int amt2 = calculateLevensteinDistance(t0, t2);
        int amt3 = calculateLevensteinDistance(t1, t2);

        if (debug) {
            System.out.println();
            System.out.println("t0 = " + t0 + " --> " + amt1);
            System.out.println("t1 = " + t1 + " --> " + amt2);
            System.out.println("t2 = " + t2 + " --> " + amt3);
            System.out.println();
        }


        int finalScore = Math.max(Math.max(amt1, amt2), amt3);
        return finalScore;
    }

    public static int calculateLevensteinDistance(String s1, String s2) {
        int distance = StringUtils.getLevenshteinDistance(s1, s2);
        double ratio = ((double) distance) / (Math.max(s1.length(), s2.length()));
        return 100 - new Double(ratio * 100).intValue();
    }

    public static String escapeString(String token) {

        StringBuffer s = new StringBuffer(token.length());

        CharacterIterator it = new StringCharacterIterator(token);
        for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
            switch (ch) {
                // '-,)(!`\":/][?;~><
                case '\'':
                case '/':
                case '\\':
                case '-':
                case ',':
                case ')':
                case '(':
                case '!':
                case '`':
                case '\"':
                case ':':
                case ']':
                case '[':
                case '?':
                case ';':
                case '~':
                case '<':
                case '>':
                    s.append(" ");
                    break;
                default:
                    s.append(ch);
                    break;
            }
        }

        token = s.toString();
        return token;
    }
    public static int getIndexOfMax(List<Integer> list){
        int max = 0;
        int maxIndex = 0;
        for ( int i = 0; i <list.size(); i ++){
            if (list.get(i) > max){
                max = list.get(i);
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
