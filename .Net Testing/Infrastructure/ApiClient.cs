using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text.Json;

namespace EHMS.DotNet10.Tests.Infrastructure;

public sealed class ApiClient : IDisposable
{
    private readonly HttpClient _http;

    public ApiClient(string? bearerToken = null)
    {
        _http = new HttpClient
        {
            BaseAddress = new Uri(TestSettings.GatewayBaseUrl),
            Timeout = TestSettings.HttpTimeout
        };

        _http.DefaultRequestHeaders.Accept.Add(
            new MediaTypeWithQualityHeaderValue("application/json"));

        if (!string.IsNullOrWhiteSpace(bearerToken))
        {
            _http.DefaultRequestHeaders.Authorization =
                new AuthenticationHeaderValue("Bearer", bearerToken);
        }
    }

    public Task<HttpResponseMessage> GetAsync(string path) =>
        _http.GetAsync(path);

    public Task<HttpResponseMessage> DeleteAsync(string path) =>
        _http.DeleteAsync(path);

    public Task<HttpResponseMessage> PostAsync<T>(string path, T body) =>
        _http.PostAsJsonAsync(path, body);

    public Task<HttpResponseMessage> PutAsync<T>(string path, T body) =>
        _http.PutAsJsonAsync(path, body);

    public Task<HttpResponseMessage> PatchAsync<T>(string path, T body)
    {
        var request = new HttpRequestMessage(HttpMethod.Patch, path)
        {
            Content = JsonContent.Create(body)
        };
        return _http.SendAsync(request);
    }

    public async Task<JsonDocument> ReadJsonAsync(HttpResponseMessage response)
    {
        var text = await response.Content.ReadAsStringAsync();
        return JsonDocument.Parse(text);
    }

    public static async Task AssertSuccessAsync(
        HttpResponseMessage response,
        string operation)
    {
        if (response.IsSuccessStatusCode)
            return;

        var body = await response.Content.ReadAsStringAsync();

        Assert.Fail(
            $"{operation} failed. HTTP {(int)response.StatusCode} " +
            $"{response.StatusCode}\nResponse: {body}");
    }

    public void Dispose() => _http.Dispose();
}
