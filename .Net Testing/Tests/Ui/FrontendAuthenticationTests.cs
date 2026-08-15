using System.Text.RegularExpressions;
using Microsoft.Playwright;
using Microsoft.Playwright.Xunit.v3;

namespace EHMS.DotNet10.Tests.Tests.Ui;

public class FrontendAuthenticationTests : PageTest
{
    [Fact]
    public async Task User_can_register_and_reach_dashboard()
    {
        var email =
            $"ui-{Guid.NewGuid():N}@example.com";

        await Page.GotoAsync(
            $"{TestSettings.FrontendBaseUrl}/register");

        await Page
            .GetByPlaceholder("Pranshu")
            .FillAsync("DotNet");

        await Page
            .GetByPlaceholder("Kothiyal")
            .FillAsync("Tester");

        await Page
            .GetByPlaceholder("name@example.com")
            .FillAsync(email);

        await Page
            .GetByPlaceholder("••••••••")
            .FillAsync("Testing123!");

        await Page
            .GetByRole(
                AriaRole.Button,
                new()
                {
                    Name = "Create account"
                })
            .ClickAsync();

        await Expect(Page)
            .ToHaveURLAsync(
                new Regex("/app$"),
                new()
                {
                    Timeout = 15_000
                });

        await Expect(
            Page.GetByText(
                "Ask AI concierge"))
            .ToBeVisibleAsync();
    }

    [Fact]
    public async Task Invalid_login_should_stay_on_login_page()
    {
        await Page.GotoAsync(
            $"{TestSettings.FrontendBaseUrl}/login");

        await Page
            .GetByPlaceholder("name@example.com")
            .FillAsync(
                $"missing-{Guid.NewGuid():N}@example.com");

        await Page
            .GetByPlaceholder("••••••••")
            .FillAsync("WrongPassword123!");

        await Page
            .GetByRole(
                AriaRole.Button,
                new()
                {
                    Name = "Sign in securely"
                })
            .ClickAsync();

        await Expect(Page)
            .ToHaveURLAsync(
                new Regex("/login$"));

        await Expect(
            Page.GetByRole(
                AriaRole.Heading,
                new()
                {
                    Name = "Sign in to StayOps."
                }))
            .ToBeVisibleAsync();
    }
}
