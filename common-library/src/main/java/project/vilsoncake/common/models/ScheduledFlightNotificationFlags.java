package project.vilsoncake.common.models;

public record ScheduledFlightNotificationFlags(
    boolean notifiedDelayed,
    boolean notifiedCancelled,
    boolean notifiedDiverted,
    boolean notifiedArrivingSoon) {}
