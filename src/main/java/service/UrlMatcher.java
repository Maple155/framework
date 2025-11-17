package service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlMatcher {
    
    public static class MatchResult {
        private UrlInfo urlInfo;
        private Map<String, String> parameters;
        
        public MatchResult(UrlInfo urlInfo, Map<String, String> parameters) {
            this.urlInfo = urlInfo;
            this.parameters = parameters;
        }
        
        public UrlInfo getUrlInfo() {
            return urlInfo;
        }
        
        public Map<String, String> getParameters() {
            return parameters;
        }
    }
    
    public static MatchResult findMatch(String path, Map<String, UrlInfo> urlMap) {
        
        if (urlMap.containsKey(path)) {
            return new MatchResult(urlMap.get(path), new HashMap<>());
        }
        
        for (Map.Entry<String, UrlInfo> entry : urlMap.entrySet()) {
            String urlPattern = entry.getKey();
            UrlInfo urlInfo = entry.getValue();
            
            if (urlPattern.contains("{") && urlPattern.contains("}")) {
                Map<String, String> params = matchUrlWithParameters(path, urlPattern);
                
                if (params != null) {
                    return new MatchResult(urlInfo, params);
                }
            }
        }
        
        return null;
    }
    
    private static Map<String, String> matchUrlWithParameters(String path, String urlPattern) {
        Map<String, String> parameters = new HashMap<>();
        
        Pattern paramPattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher paramMatcher = paramPattern.matcher(urlPattern);
        
        String regex = urlPattern;
        while (paramMatcher.find()) {
            String paramName = paramMatcher.group(1);
            regex = regex.replace("{" + paramName + "}", "([^/]+)");
        }
        
        regex = "^" + regex + "$";
        
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(path);
        
        if (matcher.matches()) {
            paramMatcher.reset(); 
            int groupIndex = 1;
            
            while (paramMatcher.find()) {
                String paramName = paramMatcher.group(1);
                String paramValue = matcher.group(groupIndex);
                parameters.put(paramName, paramValue);
                groupIndex++;
            }
            
            return parameters;
        }
        
        return null;
    }
}