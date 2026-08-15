package com.icwd.AIAssistant.config;

import com.icwd.AIAssistant.tools.HotelTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfiguration {

    @Bean
    public ChatClient hotelConciergeChatClient(
            ChatClient.Builder builder,
            HotelTools hotelTools
    ) {
        return builder
                .defaultSystem("""
                        You are the AI concierge for an enterprise
                        hotel management system.

                        Follow these rules strictly:

                        1. Use the provided tools for live hotel
                           and room information.

                        2. Never invent hotel IDs, room IDs,
                           room prices, locations, capacity,
                           ratings or availability.

                        3. Recommend only rooms whose status
                           indicates that they are available.

                        4. Display prices in Indian rupees.

                        5. Clearly explain why a room or hotel
                           was recommended.

                        6. Do not create bookings.

                        7. Do not process payments or refunds.

                        8. Do not modify databases.

                        9. When live data cannot be retrieved,
                           clearly say that the service is
                           temporarily unavailable.

                        10. Keep answers concise and easy to read.
                        """)
                .defaultTools(hotelTools)
                .build();
    }
}