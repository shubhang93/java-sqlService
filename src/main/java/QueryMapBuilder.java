import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
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
        String SQL_FILE_EXTENSION = ".sql";
        return (dir, name) -> name.toLowerCase().endsWith(SQL_FILE_EXTENSION);
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
                System.out.println("Lines -->" + lines);
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
        String QUERY_SPLITTER_REGEX = "--\\s+name\\s*: *";
        return Arrays.stream(bulkQueryString.trim().split(QUERY_SPLITTER_REGEX))
                .filter(st -> !st.isEmpty())
                .map(queryWithName -> Arrays.asList(queryWithName.split(SPACE_SEPARATOR)))
                .collect(Collectors.toList());

    }

    private String getQueryString(List<String> querySeparatedAsList, String queryName) {
        String queryString = String.join(" ", querySeparatedAsList.subList(1, querySeparatedAsList.size()));
        if (queryString.isEmpty()) {
            try {
                throw new Exception("Query name present but query statement not found for " + queryName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return queryString;
    }


    private List<String> getDuplicateQueries(Stream<String> queryNames) {
        return queryNames
                .collect(Collectors.groupingBy(it -> it))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    HashMap<String, String> getQueryMap() {
        List<List<String>> rebuiltQueries = rebuildQueries();
        System.out.println(rebuiltQueries);
        Stream<String> queryNames = rebuiltQueries.stream().map(qList -> qList.get(0));
        List<String> dupQueries = getDuplicateQueries(queryNames);
        if (dupQueries.size() > 0) {
            try {
                throw new Exception("Duplicate Queries found --> " + dupQueries);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (List<String> qList : rebuiltQueries) {
            queryMap.put(qList.get(0), getQueryString(qList, qList.get(0)));
        }
        return queryMap;
    }

}
