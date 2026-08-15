namespace EHMS.DotNet10.Tests.Infrastructure;

public static class TestSkip
{
    // xUnit v3 recognizes this message prefix as a dynamic skip.
    public static void Unless(bool condition, string reason)
    {
        if (!condition)
            throw new Exception("$XunitDynamicSkip$" + reason);
    }
}
