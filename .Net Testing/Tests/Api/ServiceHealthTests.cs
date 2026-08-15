using System.Net;
using System.Text.Json;

namespace EHMS.DotNet10.Tests.Tests.Api;

public class ServiceHealthTests
{
    public static TheoryData<string, string> Services => new()
    {
        { "API Gateway", "http://localhost:8080/actuator/health" },
        { "UserService", "http://localhost:8081/actuator/health" },
        { "HotelService", "http://localhost:8082/actuator/health" },
        { "RatingService", "http://localhost:8083/actuator/health" },
        { "BookingService", "http://localhost:8084/actuator/health" },
        { "PaymentService", "http://localhost:8085/actuator/health" },
        { "NotificationService", "http://localhost:8086/actuator/health" },
        { "EmployeeService", "http://localhost:8087/actuator/health" },
        { "InvoiceService", "http://localhost:8088/actuator/health" },
        { "RoomService", "http://localhost:8089/actuator/health" },
        { "AI Assistant", "http://localhost:8090/actuator/health" },
        { "AuthService", "http://localhost:8099/actuator/health" }
    };

    [Theory]
    [MemberData(nameof(Services))]
    public async Task SpringBoot_service_should_be_UP(
        string serviceName,
        string healthUrl)
    {
        using var client = new HttpClient
        {
            Timeout = TestSettings.HttpTimeout
        };

        HttpResponseMessage response;

        try
        {
            response = await client.GetAsync(healthUrl);
        }
        catch (Exception ex)
        {
            Assert.Fail(
                $"{serviceName} could not be reached at {healthUrl}. " +
                ex.Message);
            return;
        }

        Assert.Equal(
            HttpStatusCode.OK,
            response.StatusCode);

        var body =
            await response.Content.ReadAsStringAsync();

        using var json = JsonDocument.Parse(body);

        Assert.Equal(
            "UP",
            json.RootElement
                .GetProperty("status")
                .GetString());
    }

    [Fact]
    public async Task Eureka_dashboard_should_be_reachable()
    {
        using var client = new HttpClient
        {
            Timeout = TestSettings.HttpTimeout
        };

        var response =
            await client.GetAsync("http://localhost:8761/");

        Assert.Equal(
            HttpStatusCode.OK,
            response.StatusCode);
    }
}
