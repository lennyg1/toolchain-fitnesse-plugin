package nl.praegus.fitnesse.responders.ToolTip;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Tooltips {
    private final List<String> tooltipsCache = new ArrayList<>();

    public Tooltips(String tooltipPath, String bootstrapPath) {
        this.tooltipsCache.addAll(getFixtureTooltips(tooltipPath));
        this.tooltipsCache.addAll(getBootstrapTooltips(bootstrapPath));
    }

    public Tooltips() {
        this.tooltipsCache.addAll(getFixtureTooltips(System.getProperty("user.dir") + "/TooltipData"));
        this.tooltipsCache.addAll(getBootstrapTooltips(getBootstrapPath()));
    }

    public String getRandomTooltip() {
        if (tooltipsCache.size() != 0) {
            Random rand = new Random();
            int pickedTip = rand.nextInt(tooltipsCache.size());
            return tooltipsCache.get(pickedTip);
        } else {
            return null;
        }
    }

    private static String getBootstrapPath() {

        URL[] urls = ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs();
        Pattern regex = Pattern.compile(".*toolchain-fitnesse-plugin-\\d.\\d.\\d.*-jar-with-dependencies.jar");

        for (URL url : urls) {
            Matcher regexmatcher = regex.matcher(url.getPath());
            if (regexmatcher.find()) {
                return url.getPath();
            }
        }
        return null;
    }

    private List<String> getFixtureTooltips(String path) {
        List<String> tooltips = new ArrayList<>();
        File[] dirs = new File(path).listFiles();
        if (dirs != null) {
            for (File dir : dirs) {
                // check if File in list is directory so we wont try to listfiles from a file
                if (dir.isDirectory()) {
                    try {
                        tooltips.addAll(readTooltips(new URL("file:///" + dir.getPath() + "/Tooltips.txt")));
                    } catch (MalformedURLException e) {
                        System.out.println("couldn't find tooltips for fixture " + dir.getName());
                    }
                }
            }
        }
        return tooltips;
    }

    private List<String> getBootstrapTooltips(String path) {
        List<String> tooltips = new ArrayList<>();
        try {
            tooltips.addAll(readTooltips(new URL("jar:file:" + path + "!/fitnesse/resources/bootstrap-plus/txt/toolTipData.txt")));
        } catch (MalformedURLException e) {
            System.out.println("couldn't find bootstrap tooltips");
        }
        return tooltips;
    }

    private List<String> readTooltips(URL url) {
        try {
            InputStream inputStream = url.openStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            return bufferedReader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("couldnt read tooltips on url: " + url.getPath());
        }
        return new ArrayList<>();
    }
}
