# lomo_vidaud_player
For Lomotif Android Test

# General Requirements

* Follow the Material Design guidelines when designing your app. Use ConstraintLayout as your root view.
* You may use any 3rd party libraries to help you complete the tasks.
* Support only Portrait orientation.
* Create a private repository (Github, Bitbucket, etc.) for each tasks. Commit often and add meaningful message.

# Code Tasks

## Image Gallery

1. UI
* Display a gallery of image in a staggered grid layout (2 columns baseline, increase number of columns for larger screens).
* When user taps an image, open a new Fragment/Dialog that show a higher resolution of the selected image along with other relevant details (see the api).
* On the new Fragment/Dialog, there must be a Download button.
* When user taps the download button, display the download progress as a Notification.

2. Media Player
* Use this api https://pixabay.com/api/?key=10961674-bf47eb00b05f514cdd08f6e11&page=1 for fetching the images.
* Handle pagination. (Assume that there's at least 3 pages to access)
* Download the image in background. User must be able to navigate to other parts of the app while an image is being downloaded.
Save the image in device's Download folder.
