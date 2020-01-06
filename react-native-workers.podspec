require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name         = "react-native-workers"
  s.version      = "0.6.6-gl-012"
  s.summary      = "React Native web workers"
  s.license      = "MIT"

  s.authors      = "Garrett Mitchell"
  s.homepage     = "https://github.com/Ginger-Labs/react-native-workers"
  s.platform     = :ios, "9.0"

  s.source       = { :git => "https://github.com/react-native-community/react-native-webview.git", :tag => "#{s.version}" }
  s.source_files  = "ios/**/*.{h,m}"

  s.dependency 'React'
end
