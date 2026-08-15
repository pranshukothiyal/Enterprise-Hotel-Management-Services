using System.Net;
using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text.Json;

namespace EHMS.DotNet10.Tests.Tests.Api;

public class AuthenticationAndJwtTests
{
    [Fact]
    public async Task Register_login_and_validate_should_work()
    {
        var email =
            $"auth-flow-{Guid.NewGuid():N}@example.com";

        const string password = "Testing123!";

        using var client = new HttpClient
        {
            BaseAddress = new Uri(TestSettings.GatewayBaseUrl),
            Timeout = TestSettings.HttpTimeout
        };

        var register =
            await client.PostAsJsonAsync(
                "/api/v1/auth/register",
                new
                {
                    firstName = "System",
                    lastName = "Test",
                    email,
                    password
                });

        Assert.Equal(
            HttpStatusCode.Created,
            register.StatusCode);

        var login =
            await client.PostAsJsonAsync(
                "/api/v1/auth/authenticate",
                new { email, password });

        Assert.Equal(
            HttpStatusCode.OK,
            login.StatusCode);

        var loginJson =
            await login.Content.ReadFromJsonAsync<JsonElement>();

        Assert.True(
            loginJson.TryGetProperty(
                "token",
                out var tokenElement));

        var token = tokenElement.GetString();

        Assert.False(
            string.IsNullOrWhiteSpace(token));

        Assert.True(
            loginJson.TryGetProperty(
                "role",
                out var roleElement));

        Assert.Equal(
            "GUEST",
            roleElement.GetString());

        client.DefaultRequestHeaders.Authorization =
            new AuthenticationHeaderValue(
                "Bearer",
                token);

        var validate =
            await client.GetAsync(
                "/api/v1/auth/validate");

        Assert.Equal(
            HttpStatusCode.OK,
            validate.StatusCode);
    }

    [Theory]
    [InlineData("/users")]
    [InlineData("/bookings")]
    [InlineData("/payments")]
    public async Task Protected_routes_should_return_401_without_JWT(
        string path)
    {
        using var client = new HttpClient
        {
            BaseAddress = new Uri(TestSettings.GatewayBaseUrl),
            Timeout = TestSettings.HttpTimeout
        };

        var response =
            await client.GetAsync(path);

        Assert.Equal(
            HttpStatusCode.Unauthorized,
            response.StatusCode);
    }

    [Theory]
    [InlineData("/users")]
    [InlineData("/bookings")]
    [InlineData("/payments")]
    public async Task Protected_routes_should_accept_valid_JWT(
        string path)
    {
        var token =
            await Infrastructure.AuthHelper
                .GetGuestTokenAsync();

        using var client =
            new Infrastructure.ApiClient(token);

        var response =
            await client.GetAsync(path);

        await Infrastructure.ApiClient
            .AssertSuccessAsync(
                response,
                $"GET {path} with valid JWT");
    }

    [Fact]
    public async Task Invalid_JWT_should_be_rejected()
    {
        using var client = new HttpClient
        {
            BaseAddress = new Uri(TestSettings.GatewayBaseUrl),
            Timeout = TestSettings.HttpTimeout
        };

        client.DefaultRequestHeaders.Authorization =
            new AuthenticationHeaderValue(
                "Bearer",
                "not-a-valid-jwt");

        var response =
            await client.GetAsync("/payments");

        Assert.Equal(
            HttpStatusCode.Unauthorized,
            response.StatusCode);
    }
}
