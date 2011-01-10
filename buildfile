repositories.remote << 'http://www.ibiblio.org/maven2'


require 'time'

THIS_VERSION = '1.0.10-r220'

ant_layout = Layout.new

ant_layout[:source, :main, :java] = 'src'
ant_layout[:source, :main, :resources] = 'resource'
ant_layout[:source, :test, :java] = 'test'

desc 'jconvert'
define 'jconvert', :layout=>ant_layout do
  project.group = 'com.edsdev'
  project.version = THIS_VERSION
  package :sources
  package :javadoc
  package(:jar).with :manifest=>
  { 
    'Project' => project.id,
    'Version' => THIS_VERSION,
    'Creation' => Time.now.strftime("%a, %d %b %Y %H:%M:%S %z")
  }

end
