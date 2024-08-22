/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.workspace.api.chat.samples;

import com.google.protobuf.util.JsonFormat;
// [START chat_update_message_app_cred]
import com.google.chat.v1.ChatServiceClient;
import com.google.chat.v1.UpdateMessageRequest;
import com.google.chat.v1.Message;
import com.google.protobuf.FieldMask;

// This sample shows how to update message with app credential.
public class UpdateMessageAppCred {

  public static void main(String[] args) throws Exception {
    try (ChatServiceClient chatServiceClient =
        AuthenticationUtils.createClientWithAppCredentials()) {
      UpdateMessageRequest.Builder request = UpdateMessageRequest.newBuilder()
        .setMessage(Message.newBuilder()
          // replace SPACE_NAME and MESSAGE_NAME here
          .setName("spaces/SPACE_NAME/messages/MESSAGE_NAME")
          .setText("Updated with app credential!"))
        .setUpdateMask(FieldMask.newBuilder()
          // The field paths to update.
          .addPaths("text"));
      Message response = chatServiceClient.updateMessage(request.build());

      System.out.println(JsonFormat.printer().print(response));
    }
  }
}
// [END chat_update_message_app_cred]
