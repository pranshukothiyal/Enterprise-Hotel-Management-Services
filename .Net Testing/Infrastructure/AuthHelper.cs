using System.Net;
using System.Net.Http.Json;
using System.Text.Json;

namespace EHMS.DotNet10.Tests.Infrastructure;

public static class AuthHelper
{
    private static readonly SemaphoreSlim Gate = new(1, 1);
    private static string? _token;

    public static async Task<string> GetGuestTokenAsync()
    {
        if (!string.IsNullOrWhiteSpace(_token))
            return _token;

        await Gate.WaitAsync();

        try
        {
            if (!string.IsNullOrWhiteSpace(_token))
                return _token;

            var email =
                $"ehms-dotnet-{Guid.NewGuid():N}@example.com";

            const string password = "Testing123!";

            using var client = new HttpClient
            {
                BaseAddress = new Uri(TestSettings.GatewayBaseUrl),
                Timeout = TestSettings.HttpTimeout
            };

            var registerBody = new
            {
                firstName = "DotNet",
                lastName = "Tester",
                email,
                password
            };

            var register =
                await client.PostAsJsonAsync(
                    "/api/v1/auth/register",
                    registerBody);

            if (register.StatusCode != HttpStatusCode.Created)
            {
                var registerText =
                    await register.Content.ReadAsStringAsync();

                Assert.Fail(
                    "Could not create the test user. " +
                    $"HTTP {(int)register.StatusCode}. " +
                    registerText);
            }

            var login =
                await client.PostAsJsonAsync(
                    "/api/v1/auth/authenticate",
                    new { email, password });

            if (!login.IsSuccessStatusCode)
            {
                var loginText =
                    await login.Content.ReadAsStringAsync();

                Assert.Fail(
                    "Could not authenticate the test user. " +
                    $"HTTP {(int)login.StatusCode}. " +
                    loginText);
            }

            var json =
                await login.Content.ReadFromJsonAsync<JsonElement>();

            Assert.True(
                json.TryGetProperty("token", out var tokenElement),
                "Authentication response did not contain 'token'.");

            _token = tokenElement.GetString();

            Assert.False(
                string.IsNullOrWhiteSpace(_token),
                "Authentication returned an empty JWT.");

            return _token!;
        }
        finally
        {
            Gate.Release();
        }
    }
}
