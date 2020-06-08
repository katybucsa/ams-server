package ro.ubbcluj.cs.ams.assignment.controller;

public class SentinelFallback {

   // @Cacheable(value = "splinkCache", key = "{#subjectId,#activityTypeId,#professorUsername}")
    public static boolean linkVerificationFallback(String subjectId, int activityTypeId, String professorUsername) {

        System.out.println("Fallback subject service call");
        return false;
    }
}
