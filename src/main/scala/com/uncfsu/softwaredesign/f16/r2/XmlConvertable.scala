package com.uncfsu.softwaredesign.f16.r2

import scala.xml.Node

/**
 * Denotes an object as being convertable to xml
 * 
 */
trait XmlConvertable {
  
  def toXml: Node
  
}