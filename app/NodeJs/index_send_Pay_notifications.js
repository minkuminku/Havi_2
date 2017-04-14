'use strict';
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendPayNotification = functions.database.ref('/users/{userId}/tasks/{taskId}/status').onWrite(event => {
  const status = event.data.val();
  const userId = event.params.userId;
  const taskId = event.params.taskId;
  // If un-follow we exit the function.
  if(status == 'PAID'){
    console.log('We have a PAID status:', status, 'for user:', userId);
    const getDeviceToken = admin.database().ref(`/users/${userId}/token`).once('value');

  // Get the follower profile.
    const getUserProfilePromise = admin.auth().getUser(userId);

    console.log('Device Token:', getDeviceToken);
    console.log('User Profile:', getUserProfilePromise);

    return Promise.all([getDeviceToken, getUserProfilePromise]).then(results => {
       const tokensSnapshot = results[0];
       const userProfile = results[1];


        // Notification details.
        console.log('Device Token:', tokensSnapshot.val());
        console.log('User Profile:', userProfile);


    // Send notifications to all tokens.

// Notification details.
        const payload = {
          notification: {
            title: 'Congratulations!! You have received Payout from Havi',
            body: 'Please check tasks for more details.'
           // body: `${userProfile.displayName} has received money from `
          }
        };

    // Listing all tokens.
       const tokens = tokensSnapshot.val();

    // Send notifications to all tokens.
    return admin.messaging().sendToDevice(tokens, payload).then(response => {
      // For each message check if there was an error.
      const tokensToRemove = [];
      response.results.forEach((result, index) => {
        const error = result.error;
        if (error) {
          console.error('Failure sending notification to', tokens, error);
        }
          console.log('SENT MESSAGE TO', tokens);
      });

      return Promise.all(tokensToRemove);

    });

  });

 }
 else
 {
   console.log('Nothing to do, Status is :', status, 'for user:', userId);
 }

});

