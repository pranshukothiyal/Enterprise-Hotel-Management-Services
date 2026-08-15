using System.Text.RegularExpressions;
using Microsoft.Playwright;
using Microsoft.Playwright.Xunit.v3;

namespace EHMS.DotNet10.Tests.Tests.Ui;

public class FrontendSmokeTests : PageTest
{
    [Fact]
    public async Task Landing_page_should_render()
    {
        await Page.GotoAsync(
            TestSettings.FrontendBaseUrl);

        await Expect(
            Page.GetByRole(
                AriaRole.Link,
                new() { Name = "Sign in" }))
            .ToBeVisibleAsync();

        await Expect(Page)
            .ToHaveURLAsync(
                new Regex(
                    Regex.Escape(
                        TestSettings.FrontendBaseUrl)));
    }

    [Fact]
    public async Task Explore_page_should_render_and_call_backend()
    {
        IResponse? hotelsResponse = null;
        IResponse? roomsResponse = null;

        Page.Response += (_, response) =>
        {
            if (response.Url.Contains(
                    "/gateway/hotels",
                    StringComparison.OrdinalIgnoreCase))
                hotelsResponse = response;

            if (response.Url.Contains(
                    "/gateway/rooms",
                    StringComparison.OrdinalIgnoreCase))
                roomsResponse = response;
        };

        await Page.GotoAsync(
            $"{TestSettings.FrontendBaseUrl}/explore");

        await Expect(
            Page.GetByRole(
                AriaRole.Heading,
                new()
                {
                    Name = "Find your next stay."
                }))
            .ToBeVisibleAsync();

        await Page.WaitForTimeoutAsync(1500);

        Assert.NotNull(hotelsResponse);
        Assert.NotNull(roomsResponse);

        Assert.True(
            hotelsResponse!.Ok,
            $"Frontend /gateway/hotels returned HTTP {hotelsResponse.Status}");

        Assert.True(
            roomsResponse!.Ok,
            $"Frontend /gateway/rooms returned HTTP {roomsResponse.Status}");
    }

    [Fact]
    public async Task Protected_app_route_should_redirect_anonymous_user()
    {
        await Page.GotoAsync(
            $"{TestSettings.FrontendBaseUrl}/app");

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
