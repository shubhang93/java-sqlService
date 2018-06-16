import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class QueryMapBuilder {

    private String sqlFilesPath;
    private HashMap<String, String> queryMap = new HashMap<>();

    private final String SPACE_SEPARATOR = " ";

    QueryMapBuilder(String sqlFilesPath) {
        this.sqlFilesPath = sqlFilesPath;
    }

    private FilenameFilter filterSqlFiles() {
        return (dir, name) -> name.toLowerCase().endsWith(".sql");
    }


    private Stream<File> getFileStream() {
        File sqlFilesFolder = new File(sqlFilesPath);
        File[] files = sqlFilesFolder.listFiles(filterSqlFiles());
        assert files != null;
        return Arrays.stream(files);
    }

    private Stream<Stream<String>> getUnprocessedQueries() {
        Stream<File> fileStream = getFileStream();
        List<List<String>> unprocessedQueries = new ArrayList<>();
        fileStream.forEach(file -> {
            try {
                List<String> lines = FileUtils.readLines(file, "UTF-8");
                unprocessedQueries.add(lines);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return unprocessedQueries.stream()
                .map(qList -> qList.stream()
                        .filter(q -> !q.equals("")));
    }

    private List<List<String>> rebuildQueries() {
        Stream<Stream<String>> unprocessedQueries = getUnprocessedQueries();
        Stream<String> flattenedQueries = unprocessedQueries.flatMap(queriesForFile -> queriesForFile);
        String bulkQueryString = String.join(" ", flattenedQueries.collect(Collectors.toList()));
        String QUERY_SPLITTER_REGEX = "-- name: *";
        return Arrays.stream(bulkQueryString.trim().split(QUERY_SPLITTER_REGEX))
                .filter(st -> !st.isEmpty())
                .map(queryWithName -> Arrays.asList(queryWithName.split(SPACE_SEPARATOR)))
                .collect(Collectors.toList());

    }

    private String getQueryString(List<String> querySeparatedAsList) {
        return String.join(" ", querySeparatedAsList.subList(1, querySeparatedAsList.size()));
    }

    HashMap<String, String> getQueryMap() {
        List<List<String>> rebuiltQueries = rebuildQueries();
        for (List<String> qList : rebuiltQueries) {
            queryMap.put(qList.get(0), getQueryString(qList));
        }

        return queryMap;
    }

}
