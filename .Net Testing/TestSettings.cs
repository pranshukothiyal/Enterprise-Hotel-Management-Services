namespace EHMS.DotNet10.Tests;

public static class TestSettings
{
    public static string GatewayBaseUrl =>
        Environment.GetEnvironmentVariable("EHMS_GATEWAY_URL")
        ?? "http://localhost:8080";

    public static string FrontendBaseUrl =>
        Environment.GetEnvironmentVariable("EHMS_FRONTEND_URL")
        ?? "http://localhost:5173";

    public static bool RunAiChat =>
        IsTrue(Environment.GetEnvironmentVariable("EHMS_RUN_AI_CHAT"));

    public static bool RunRazorpay =>
        IsTrue(Environment.GetEnvironmentVariable("EHMS_RUN_RAZORPAY"));

    public static TimeSpan HttpTimeout => TimeSpan.FromSeconds(30);

    private static bool IsTrue(string? value) =>
        string.Equals(value, "true", StringComparison.OrdinalIgnoreCase)
        || value == "1"
        || string.Equals(value, "yes", StringComparison.OrdinalIgnoreCase);
}
