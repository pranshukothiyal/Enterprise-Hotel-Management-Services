using System.Net;
using System.Net.Http.Json;
using System.Text.Json;
using EHMS.DotNet10.Tests.Infrastructure;

namespace EHMS.DotNet10.Tests.Tests.Api;

public class BookingPaymentIntegrationTests
{
    [Fact]
    public async Task Booking_create_and_payment_details_should_work()
    {
        var token =
            await AuthHelper.GetGuestTokenAsync();

        using var client =
            new ApiClient(token);

        var today = DateOnly.FromDateTime(
            DateTime.UtcNow.AddDays(7));

        var tomorrow = today.AddDays(2);

        var createBooking =
            await client.PostAsync(
                "/bookings",
                new
                {
                    userId = $"TEST-USER-{Guid.NewGuid():N}",
                    hotelId = "TEST-HOTEL",
                    roomId = $"TEST-ROOM-{Guid.NewGuid():N}",
                    checkInDate = today.ToString("yyyy-MM-dd"),
                    checkOutDate = tomorrow.ToString("yyyy-MM-dd"),
                    totalAmount = 1500.00,
                    bookingStatus = "PENDING"
                });

        Assert.Equal(
            HttpStatusCode.Created,
            createBooking.StatusCode);

        var bookingJson =
            await createBooking.Content
                .ReadFromJsonAsync<JsonElement>();

        var bookingId =
            bookingJson.GetProperty("bookingId")
                .GetString();

        Assert.False(
            string.IsNullOrWhiteSpace(bookingId));

        var paymentDetails =
            await client.GetAsync(
                $"/bookings/{bookingId}/payment-details");

        await ApiClient.AssertSuccessAsync(
            paymentDetails,
            "Booking payment-details integration");

        var detailsJson =
            await paymentDetails.Content
                .ReadFromJsonAsync<JsonElement>();

        Assert.Equal(
            bookingId,
            detailsJson
                .GetProperty("bookingId")
                .GetString());

        Assert.Equal(
            1500.00,
            detailsJson
                .GetProperty("totalAmount")
                .GetDouble(),
            2);
    }

    [Fact]
    public async Task Razorpay_order_creation_should_work_when_enabled()
    {
        TestSkip.Unless(
            TestSettings.RunRazorpay,
            "Set EHMS_RUN_RAZORPAY=true to run the live Razorpay Test Mode integration test.");

        var token =
            await AuthHelper.GetGuestTokenAsync();

        using var client =
            new ApiClient(token);

        var checkIn =
            DateOnly.FromDateTime(
                DateTime.UtcNow.AddDays(14));

        var checkOut =
            checkIn.AddDays(2);

        var booking =
            await client.PostAsync(
                "/bookings",
                new
                {
                    userId = $"RZP-USER-{Guid.NewGuid():N}",
                    hotelId = "RZP-TEST-HOTEL",
                    roomId = $"RZP-ROOM-{Guid.NewGuid():N}",
                    checkInDate = checkIn.ToString("yyyy-MM-dd"),
                    checkOutDate = checkOut.ToString("yyyy-MM-dd"),
                    totalAmount = 100.00,
                    bookingStatus = "PENDING"
                });

        await ApiClient.AssertSuccessAsync(
            booking,
            "Create booking for Razorpay test");

        var bookingJson =
            await booking.Content
                .ReadFromJsonAsync<JsonElement>();

        var bookingId =
            bookingJson.GetProperty("bookingId")
                .GetString();

        var order =
            await client.PostAsync(
                "/payments/razorpay/orders",
                new { bookingId });

        await ApiClient.AssertSuccessAsync(
            order,
            "Create Razorpay order");

        var orderJson =
            await order.Content
                .ReadFromJsonAsync<JsonElement>();

        Assert.False(
            string.IsNullOrWhiteSpace(
                orderJson
                    .GetProperty("razorpayOrderId")
                    .GetString()));
    }
}
