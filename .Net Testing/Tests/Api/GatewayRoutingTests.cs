using EHMS.DotNet10.Tests.Infrastructure;

namespace EHMS.DotNet10.Tests.Tests.Api;

public class GatewayRoutingTests
{
    public static TheoryData<string, string> Routes => new()
    {
        { "UserService", "/users" },
        { "HotelService", "/hotels" },
        { "Room API in HotelService", "/rooms" },
        { "RatingService", "/ratings" },
        { "BookingService", "/bookings" },
        { "PaymentService", "/payments" },
        { "NotificationService", "/notifications" },
        { "EmployeeService employees", "/employees" },
        { "EmployeeService departments", "/departments" },
        { "InvoiceService", "/invoices" },
        { "RoomService offerings", "/hotelservices" },
        { "RoomService requests", "/roomservicerequests" },
        { "AI Assistant", "/api/ai/test" }
    };

    [Theory]
    [MemberData(nameof(Routes))]
    public async Task Gateway_should_route_to_each_microservice(
        string service,
        string path)
    {
        var token =
            await AuthHelper.GetGuestTokenAsync();

        using var client =
            new ApiClient(token);

        var response =
            await client.GetAsync(path);

        await ApiClient.AssertSuccessAsync(
            response,
            $"{service} through API Gateway ({path})");
    }

    [Theory]
    [InlineData("/hotels")]
    [InlineData("/rooms")]
    [InlineData("/ratings")]
    public async Task Public_catalog_routes_should_work_without_JWT(
        string path)
    {
        using var client =
            new ApiClient();

        var response =
            await client.GetAsync(path);

        await ApiClient.AssertSuccessAsync(
            response,
            $"Public GET {path}");
    }
}
