# gocd-http-notifier
HTTP based GoCD build notifier

Listens to notifications from the GoCD (15.1+) notification api, and publishes them over HTTP.
This allows you to easily handle the notifications api outside of the context of a java plugin.


## Setup
Download jar from releases & place it in /plugins/external & restart Go Server.

## Configuration
Use the go plugin notification page to configure the URL the plugin sends all the requests to.

## License
http://www.apache.org/licenses/LICENSE-2.0

## Credits
Based on the [gocd-websocket-notifier](https://github.com/matt-richardson/gocd-websocket-notifier) by Matt Richardson.
