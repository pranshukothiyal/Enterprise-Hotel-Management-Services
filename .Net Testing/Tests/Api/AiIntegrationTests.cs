using System.Net;
using System.Net.Http.Json;
using System.Text.Json;
using EHMS.DotNet10.Tests.Infrastructure;

namespace EHMS.DotNet10.Tests.Tests.Api;

public class AiIntegrationTests
{
    [Fact]
    public async Task Ai_service_test_endpoint_should_work()
    {
        var token =
            await AuthHelper.GetGuestTokenAsync();

        using var client =
            new ApiClient(token);

        var response =
            await client.GetAsync("/api/ai/test");

        Assert.Equal(
            HttpStatusCode.OK,
            response.StatusCode);

        var text =
            await response.Content.ReadAsStringAsync();

        Assert.Contains(
            "AI Assistant Service",
            text,
            StringComparison.OrdinalIgnoreCase);
    }

    [Fact]
    public async Task Ai_chat_should_return_answer_when_enabled()
    {
        TestSkip.Unless(
            TestSettings.RunAiChat,
            "Set EHMS_RUN_AI_CHAT=true and start Ollama/llama3.2 to run the real AI chat test.");

        var token =
            await AuthHelper.GetGuestTokenAsync();

        using var client =
            new ApiClient(token);

        var response =
            await client.PostAsync(
                "/api/ai/chat",
                new
                {
                    message =
                        "Which rooms are currently available?"
                });

        await ApiClient.AssertSuccessAsync(
            response,
            "Spring AI chat");

        var json =
            await response.Content
                .ReadFromJsonAsync<JsonElement>();

        Assert.True(
            json.TryGetProperty(
                "answer",
                out var answer));

        Assert.False(
            string.IsNullOrWhiteSpace(
                answer.GetString()));
    }
}
