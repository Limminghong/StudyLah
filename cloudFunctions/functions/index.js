const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

const SG_TIME = 8 * 60 * 60 * 1000; // 8 hours in advance

// Create and Deploy Your First Cloud Functions
// https://firebase.google.com/docs/functions/write-firebase-functions

exports.deleteOldData = functions.database.ref('sessions/{pushId}')
.onWrite((change) => {
    const ref = change.after.ref.parent;
    const now = Date.now() + SG_TIME;
    const oldItemsQuery = ref.orderByChild('timestamp').endAt(now);
    return oldItemsQuery.once('value').then((snapshot) => {
        const updates ={};
        snapshot.forEach(child => {
            updates[child.key] = null;
        });
        return ref.update(updates);
    });
});
