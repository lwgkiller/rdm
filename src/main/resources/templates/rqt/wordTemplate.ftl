<?xml version="1.0" encoding="utf-8"?>
<?mso-application progid="Word.Document"?>

<pkg:package xmlns:pkg="http://schemas.microsoft.com/office/2006/xmlPackage">
  <pkg:part pkg:name="/_rels/.rels" pkg:contentType="application/vnd.openxmlformats-package.relationships+xml" pkg:padding="512">
    <pkg:xmlData>
      <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
        <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties" Target="docProps/app.xml"/>
        <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties" Target="docProps/core.xml"/>
        <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
      </Relationships>
    </pkg:xmlData>
  </pkg:part>
  <pkg:part pkg:name="/word/_rels/document.xml.rels" pkg:contentType="application/vnd.openxmlformats-package.relationships+xml" pkg:padding="256">
    <pkg:xmlData>
      <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
        <Relationship Id="rId8" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/theme" Target="theme/theme1.xml"/>
        <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/settings" Target="settings.xml"/>
        <Relationship Id="rId7" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/fontTable" Target="fontTable.xml"/>
        <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
        <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/numbering" Target="numbering.xml"/>
        <Relationship Id="rId6" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/endnotes" Target="endnotes.xml"/>
        <Relationship Id="rId5" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/footnotes" Target="footnotes.xml"/>
        <Relationship Id="rId4" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/webSettings" Target="webSettings.xml"/>
      </Relationships>
    </pkg:xmlData>
  </pkg:part>
  <pkg:part pkg:name="/word/document.xml" pkg:contentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml">
    <pkg:xmlData>
      <w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main" xmlns:wpc="http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas" xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006" xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:wp14="http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing" xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing" xmlns:w10="urn:schemas-microsoft-com:office:word" xmlns:w14="http://schemas.microsoft.com/office/word/2010/wordml" xmlns:w15="http://schemas.microsoft.com/office/word/2012/wordml" xmlns:wpg="http://schemas.microsoft.com/office/word/2010/wordprocessingGroup" xmlns:wpi="http://schemas.microsoft.com/office/word/2010/wordprocessingInk" xmlns:wne="http://schemas.microsoft.com/office/word/2006/wordml" xmlns:wps="http://schemas.microsoft.com/office/word/2010/wordprocessingShape" mc:Ignorable="w14 w15 wp14">
        <w:body>
		<#list table1 as newlist>
          <w:p w:rsidR="00CF4303" w:rsidRPr="00F135B6" w:rsidRDefault="00587A70" w:rsidP="00F135B6">
            <w:pPr>
              <w:pStyle w:val="${newlist.level+1}"/>
              <w:numPr>
                <w:ilvl w:val="${newlist.level}"/>
                <w:numId w:val="2"/>
              </w:numPr>
              <w:adjustRightInd w:val="0"/>
              <w:spacing w:after="0" w:line="144" w:lineRule="auto"/>
              <w:ind w:left="284" w:hanging="284"/>
            </w:pPr>
            <w:r w:rsidRPr="00F135B6">
              <w:rPr>
                <w:rFonts w:hint="eastAsia"/>
              </w:rPr>
              <w:t>${newlist.subject}</w:t>
            </w:r>
          </w:p>
          <w:tbl>
            <w:tblPr>
              <w:tblStyle w:val="a6"/>
              <w:tblW w:w="9501" w:type="dxa"/>
              <w:tblLook w:val="04A0" w:firstRow="1" w:lastRow="0" w:firstColumn="1" w:lastColumn="0" w:noHBand="0" w:noVBand="1"/>
            </w:tblPr>
            <w:tblGrid>
              <w:gridCol w:w="2022"/>
              <w:gridCol w:w="1899"/>
              <w:gridCol w:w="1721"/>
              <w:gridCol w:w="1728"/>
              <w:gridCol w:w="2131"/>
            </w:tblGrid>
            <w:tr w:rsidR="00CF4303" w:rsidTr="00AF064C">
              <w:trPr>
                <w:trHeight w:val="243"/>
              </w:trPr>
              <w:tc>
                <w:tcPr>
                  <w:tcW w:w="2022" w:type="dxa"/>
                </w:tcPr>
                <w:p w:rsidR="00CF4303" w:rsidRDefault="00CF4303" w:rsidP="00AF064C">
                  <w:pPr>
                    <w:jc w:val="center"/>
                  </w:pPr>
                  <w:r w:rsidRPr="00CF4303">
                    <w:rPr>
                      <w:rFonts w:hint="eastAsia"/>
                    </w:rPr>
                    <w:t>需求编码</w:t>
                  </w:r>
                </w:p>
              </w:tc>
              <w:tc>
                <w:tcPr>
                  <w:tcW w:w="1899" w:type="dxa"/>
                </w:tcPr>
                <w:p w:rsidR="00CF4303" w:rsidRDefault="00CF4303" w:rsidP="00AF064C">
                  <w:pPr>
                    <w:jc w:val="center"/>
                  </w:pPr>
                  <w:r w:rsidRPr="00F135B6">
                    <w:rPr>
                      <w:rFonts w:hint="eastAsia"/>
                    </w:rPr>
                    <w:t>版本</w:t>
                  </w:r>
                </w:p>
              </w:tc>
              <w:tc>
                <w:tcPr>
                  <w:tcW w:w="1721" w:type="dxa"/>
                </w:tcPr>
                <w:p w:rsidR="00CF4303" w:rsidRDefault="00CF4303" w:rsidP="00AF064C">
                  <w:pPr>
                    <w:jc w:val="center"/>
                  </w:pPr>
                  <w:r w:rsidRPr="00F135B6">
                    <w:rPr>
                      <w:rFonts w:hint="eastAsia"/>
                    </w:rPr>
                    <w:t>负责人</w:t>
                  </w:r>
                </w:p>
              </w:tc>
              <w:tc>
                <w:tcPr>
                  <w:tcW w:w="1728" w:type="dxa"/>
                </w:tcPr>
                <w:p w:rsidR="00CF4303" w:rsidRDefault="00495C49" w:rsidP="00AF064C">
                  <w:pPr>
                    <w:jc w:val="center"/>
                  </w:pPr>
                  <w:r>
                    <w:rPr>
                      <w:rFonts w:hint="eastAsia"/>
                    </w:rPr>
                    <w:t>执行人</w:t>
                  </w:r>
                </w:p>
              </w:tc>
              <w:tc>
                <w:tcPr>
                  <w:tcW w:w="2131" w:type="dxa"/>
                </w:tcPr>
                <w:p w:rsidR="00CF4303" w:rsidRPr="00F135B6" w:rsidRDefault="00CF4303" w:rsidP="00AF064C">
                  <w:pPr>
                    <w:jc w:val="center"/>
                  </w:pPr>
                  <w:r w:rsidRPr="00F135B6">
                    <w:rPr>
                      <w:rFonts w:hint="eastAsia"/>
                    </w:rPr>
                    <w:t>审批人</w:t>
                  </w:r>
                </w:p>
              </w:tc>
            </w:tr>
            <w:tr w:rsidR="00CF4303" w:rsidTr="00AF064C">
              <w:trPr>
                <w:trHeight w:val="476"/>
              </w:trPr>
              <w:tc>
                <w:tcPr>
                  <w:tcW w:w="2022" w:type="dxa"/>
                </w:tcPr>
                <w:p w:rsidR="00CF4303" w:rsidRDefault="00CF4303" w:rsidP="00322AA6">
                  <w:r w:rsidRPr="00F135B6">
                    <w:rPr>
                      <w:rFonts w:hint="eastAsia"/>
                    </w:rPr>
                    <w:t>${newlist.reqCode}</w:t>
                  </w:r>
                </w:p>
              </w:tc>
              <w:tc>
                <w:tcPr>
                  <w:tcW w:w="1899" w:type="dxa"/>
                </w:tcPr>
                <w:p w:rsidR="00CF4303" w:rsidRDefault="00CF4303" w:rsidP="00322AA6">
                  <w:r w:rsidRPr="00F135B6">
                    <w:rPr>
                      <w:rFonts w:hint="eastAsia"/>
                    </w:rPr>
                    <w:t>${newlist.version}</w:t>
                  </w:r>
                </w:p>
              </w:tc>
              <w:tc>
                <w:tcPr>
                  <w:tcW w:w="1721" w:type="dxa"/>
                </w:tcPr>
                <w:p w:rsidR="00CF4303" w:rsidRDefault="00CF4303" w:rsidP="00322AA6">
                  <w:r w:rsidRPr="00F135B6">
                    <w:rPr>
                      <w:rFonts w:hint="eastAsia"/>
                    </w:rPr>
                    <w:t>${newlist.rep}</w:t>
                  </w:r>
                </w:p>
              </w:tc>
              <w:tc>
                <w:tcPr>
                  <w:tcW w:w="1728" w:type="dxa"/>
                </w:tcPr>
                <w:p w:rsidR="00CF4303" w:rsidRDefault="00CF4303" w:rsidP="00322AA6">
                  <w:r w:rsidRPr="00F135B6">
                    <w:rPr>
                      <w:rFonts w:hint="eastAsia"/>
                    </w:rPr>
                    <w:t>${newlist.exe}</w:t>
                  </w:r>
                </w:p>
              </w:tc>
              <w:tc>
                <w:tcPr>
                  <w:tcW w:w="2131" w:type="dxa"/>
                </w:tcPr>
                <w:p w:rsidR="00CF4303" w:rsidRDefault="00CF4303" w:rsidP="00322AA6">
                  <w:r w:rsidRPr="00F135B6">
                    <w:rPr>
                      <w:rFonts w:hint="eastAsia"/>
                    </w:rPr>
                    <w:t>${newlist.checker}</w:t>
                  </w:r>
                </w:p>
              </w:tc>
            </w:tr>
          </w:tbl>
          <w:p w:rsidR="00CF4303" w:rsidRPr="00077F40" w:rsidRDefault="00077F40" w:rsidP="00322AA6">
            <w:pPr>
              <w:rPr>
                <w:sz w:val="30"/>
                <w:szCs w:val="30"/>
              </w:rPr>
            </w:pPr>
            <w:r>
              <w:rPr>
                <w:rFonts w:hint="eastAsia"/>
                <w:color w:val="2E74B5" w:themeColor="accent1" w:themeShade="BF"/>
                <w:sz w:val="30"/>
                <w:szCs w:val="30"/>
              </w:rPr>
              <w:t>描述</w:t>
            </w:r>
            <w:r>
              <w:rPr>
                <w:rFonts w:hint="eastAsia"/>
                <w:color w:val="2E74B5" w:themeColor="accent1" w:themeShade="BF"/>
                <w:sz w:val="30"/>
                <w:szCs w:val="30"/>
              </w:rPr>
              <w:t>:</w:t>
            </w:r>
            <w:bookmarkStart w:id="0" w:name="_GoBack"/>
            <w:bookmarkEnd w:id="0"/>
            <w:r w:rsidRPr="00077F40">
              <w:rPr>
                <w:rFonts w:hint="eastAsia"/>
                <w:color w:val="2E74B5" w:themeColor="accent1" w:themeShade="BF"/>
                <w:sz w:val="30"/>
                <w:szCs w:val="30"/>
              </w:rPr>
              <w:t>${newlist.myDescp}</w:t>
            </w:r>
          </w:p>
		  
		  </#list>
          <w:sectPr w:rsidR="00CF4303" w:rsidRPr="00077F40">
            <w:pgSz w:w="11906" w:h="16838"/>
            <w:pgMar w:top="1440" w:right="1800" w:bottom="1440" w:left="1800" w:header="851" w:footer="992" w:gutter="0"/>
            <w:cols w:space="425"/>
            <w:docGrid w:type="lines" w:linePitch="312"/>
          </w:sectPr>
        </w:body>
      </w:document>
    </pkg:xmlData>
  </pkg:part>
  <pkg:part pkg:name="/word/footnotes.xml" pkg:contentType="application/vnd.openxmlformats-officedocument.wordprocessingml.footnotes+xml">
    <pkg:xmlData>
      <w:footnotes xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main" xmlns:wpc="http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas" xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006" xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:wp14="http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing" xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing" xmlns:w10="urn:schemas-microsoft-com:office:word" xmlns:w14="http://schemas.microsoft.com/office/word/2010/wordml" xmlns:w15="http://schemas.microsoft.com/office/word/2012/wordml" xmlns:wpg="http://schemas.microsoft.com/office/word/2010/wordprocessingGroup" xmlns:wpi="http://schemas.microsoft.com/office/word/2010/wordprocessingInk" xmlns:wne="http://schemas.microsoft.com/office/word/2006/wordml" xmlns:wps="http://schemas.microsoft.com/office/word/2010/wordprocessingShape" mc:Ignorable="w14 w15 wp14">
        <w:footnote w:type="separator" w:id="-1">
          <w:p w:rsidR="00EE2937" w:rsidRDefault="00EE2937" w:rsidP="00587A70">
            <w:r>
              <w:separator/>
            </w:r>
          </w:p>
        </w:footnote>
        <w:footnote w:type="continuationSeparator" w:id="0">
          <w:p w:rsidR="00EE2937" w:rsidRDefault="00EE2937" w:rsidP="00587A70">
            <w:r>
              <w:continuationSeparator/>
            </w:r>
          </w:p>
        </w:footnote>
      </w:footnotes>
    </pkg:xmlData>
  </pkg:part>
  <pkg:part pkg:name="/word/endnotes.xml" pkg:contentType="application/vnd.openxmlformats-officedocument.wordprocessingml.endnotes+xml">
    <pkg:xmlData>
      <w:endnotes xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main" xmlns:wpc="http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas" xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006" xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:wp14="http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing" xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing" xmlns:w10="urn:schemas-microsoft-com:office:word" xmlns:w14="http://schemas.microsoft.com/office/word/2010/wordml" xmlns:w15="http://schemas.microsoft.com/office/word/2012/wordml" xmlns:wpg="http://schemas.microsoft.com/office/word/2010/wordprocessingGroup" xmlns:wpi="http://schemas.microsoft.com/office/word/2010/wordprocessingInk" xmlns:wne="http://schemas.microsoft.com/office/word/2006/wordml" xmlns:wps="http://schemas.microsoft.com/office/word/2010/wordprocessingShape" mc:Ignorable="w14 w15 wp14">
        <w:endnote w:type="separator" w:id="-1">
          <w:p w:rsidR="00EE2937" w:rsidRDefault="00EE2937" w:rsidP="00587A70">
            <w:r>
              <w:separator/>
            </w:r>
          </w:p>
        </w:endnote>
        <w:endnote w:type="continuationSeparator" w:id="0">
          <w:p w:rsidR="00EE2937" w:rsidRDefault="00EE2937" w:rsidP="00587A70">
            <w:r>
              <w:continuationSeparator/>
            </w:r>
          </w:p>
        </w:endnote>
      </w:endnotes>
    </pkg:xmlData>
  </pkg:part>
  <pkg:part pkg:name="/word/theme/theme1.xml" pkg:contentType="application/vnd.openxmlformats-officedocument.theme+xml">
    <pkg:xmlData>
      <a:theme xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main" name="Office 主题">
        <a:themeElements>
          <a:clrScheme name="Office">
            <a:dk1>
              <a:sysClr val="windowText" lastClr="000000"/>
            </a:dk1>
            <a:lt1>
              <a:sysClr val="window" lastClr="FFFFFF"/>
            </a:lt1>
            <a:dk2>
              <a:srgbClr val="44546A"/>
            </a:dk2>
            <a:lt2>
              <a:srgbClr val="E7E6E6"/>
            </a:lt2>
            <a:accent1>
              <a:srgbClr val="5B9BD5"/>
            </a:accent1>
            <a:accent2>
              <a:srgbClr val="ED7D31"/>
            </a:accent2>
            <a:accent3>
              <a:srgbClr val="A5A5A5"/>
            </a:accent3>
            <a:accent4>
              <a:srgbClr val="FFC000"/>
            </a:accent4>
            <a:accent5>
              <a:srgbClr val="4472C4"/>
            </a:accent5>
            <a:accent6>
              <a:srgbClr val="70AD47"/>
            </a:accent6>
            <a:hlink>
              <a:srgbClr val="0563C1"/>
            </a:hlink>
            <a:folHlink>
              <a:srgbClr val="954F72"/>
            </a:folHlink>
          </a:clrScheme>
          <a:fontScheme name="Office">
            <a:majorFont>
              <a:latin typeface="Calibri Light" panose="020F0302020204030204"/>
              <a:ea typeface=""/>
              <a:cs typeface=""/>
              <a:font script="Jpan" typeface="ＭＳ ゴシック"/>
              <a:font script="Hang" typeface="맑은 고딕"/>
              <a:font script="Hans" typeface="宋体"/>
              <a:font script="Hant" typeface="新細明體"/>
              <a:font script="Arab" typeface="Times New Roman"/>
              <a:font script="Hebr" typeface="Times New Roman"/>
              <a:font script="Thai" typeface="Angsana New"/>
              <a:font script="Ethi" typeface="Nyala"/>
              <a:font script="Beng" typeface="Vrinda"/>
              <a:font script="Gujr" typeface="Shruti"/>
              <a:font script="Khmr" typeface="MoolBoran"/>
              <a:font script="Knda" typeface="Tunga"/>
              <a:font script="Guru" typeface="Raavi"/>
              <a:font script="Cans" typeface="Euphemia"/>
              <a:font script="Cher" typeface="Plantagenet Cherokee"/>
              <a:font script="Yiii" typeface="Microsoft Yi Baiti"/>
              <a:font script="Tibt" typeface="Microsoft Himalaya"/>
              <a:font script="Thaa" typeface="MV Boli"/>
              <a:font script="Deva" typeface="Mangal"/>
              <a:font script="Telu" typeface="Gautami"/>
              <a:font script="Taml" typeface="Latha"/>
              <a:font script="Syrc" typeface="Estrangelo Edessa"/>
              <a:font script="Orya" typeface="Kalinga"/>
              <a:font script="Mlym" typeface="Kartika"/>
              <a:font script="Laoo" typeface="DokChampa"/>
              <a:font script="Sinh" typeface="Iskoola Pota"/>
              <a:font script="Mong" typeface="Mongolian Baiti"/>
              <a:font script="Viet" typeface="Times New Roman"/>
              <a:font script="Uigh" typeface="Microsoft Uighur"/>
              <a:font script="Geor" typeface="Sylfaen"/>
            </a:majorFont>
            <a:minorFont>
              <a:latin typeface="Calibri" panose="020F0502020204030204"/>
              <a:ea typeface=""/>
              <a:cs typeface=""/>
              <a:font script="Jpan" typeface="ＭＳ 明朝"/>
              <a:font script="Hang" typeface="맑은 고딕"/>
              <a:font script="Hans" typeface="宋体"/>
              <a:font script="Hant" typeface="新細明體"/>
              <a:font script="Arab" typeface="Arial"/>
              <a:font script="Hebr" typeface="Arial"/>
              <a:font script="Thai" typeface="Cordia New"/>
              <a:font script="Ethi" typeface="Nyala"/>
              <a:font script="Beng" typeface="Vrinda"/>
              <a:font script="Gujr" typeface="Shruti"/>
              <a:font script="Khmr" typeface="DaunPenh"/>
              <a:font script="Knda" typeface="Tunga"/>
              <a:font script="Guru" typeface="Raavi"/>
              <a:font script="Cans" typeface="Euphemia"/>
              <a:font script="Cher" typeface="Plantagenet Cherokee"/>
              <a:font script="Yiii" typeface="Microsoft Yi Baiti"/>
              <a:font script="Tibt" typeface="Microsoft Himalaya"/>
              <a:font script="Thaa" typeface="MV Boli"/>
              <a:font script="Deva" typeface="Mangal"/>
              <a:font script="Telu" typeface="Gautami"/>
              <a:font script="Taml" typeface="Latha"/>
              <a:font script="Syrc" typeface="Estrangelo Edessa"/>
              <a:font script="Orya" typeface="Kalinga"/>
              <a:font script="Mlym" typeface="Kartika"/>
              <a:font script="Laoo" typeface="DokChampa"/>
              <a:font script="Sinh" typeface="Iskoola Pota"/>
              <a:font script="Mong" typeface="Mongolian Baiti"/>
              <a:font script="Viet" typeface="Arial"/>
              <a:font script="Uigh" typeface="Microsoft Uighur"/>
              <a:font script="Geor" typeface="Sylfaen"/>
            </a:minorFont>
          </a:fontScheme>
          <a:fmtScheme name="Office">
            <a:fillStyleLst>
              <a:solidFill>
                <a:schemeClr val="phClr"/>
              </a:solidFill>
              <a:gradFill rotWithShape="1">
                <a:gsLst>
                  <a:gs pos="0">
                    <a:schemeClr val="phClr">
                      <a:lumMod val="110000"/>
                      <a:satMod val="105000"/>
                      <a:tint val="67000"/>
                    </a:schemeClr>
                  </a:gs>
                  <a:gs pos="50000">
                    <a:schemeClr val="phClr">
                      <a:lumMod val="105000"/>
                      <a:satMod val="103000"/>
                      <a:tint val="73000"/>
                    </a:schemeClr>
                  </a:gs>
                  <a:gs pos="100000">
                    <a:schemeClr val="phClr">
                      <a:lumMod val="105000"/>
                      <a:satMod val="109000"/>
                      <a:tint val="81000"/>
                    </a:schemeClr>
                  </a:gs>
                </a:gsLst>
                <a:lin ang="5400000" scaled="0"/>
              </a:gradFill>
              <a:gradFill rotWithShape="1">
                <a:gsLst>
                  <a:gs pos="0">
                    <a:schemeClr val="phClr">
                      <a:satMod val="103000"/>
                      <a:lumMod val="102000"/>
                      <a:tint val="94000"/>
                    </a:schemeClr>
                  </a:gs>
                  <a:gs pos="50000">
                    <a:schemeClr val="phClr">
                      <a:satMod val="110000"/>
                      <a:lumMod val="100000"/>
                      <a:shade val="100000"/>
                    </a:schemeClr>
                  </a:gs>
                  <a:gs pos="100000">
                    <a:schemeClr val="phClr">
                      <a:lumMod val="99000"/>
                      <a:satMod val="120000"/>
                      <a:shade val="78000"/>
                    </a:schemeClr>
                  </a:gs>
                </a:gsLst>
                <a:lin ang="5400000" scaled="0"/>
              </a:gradFill>
            </a:fillStyleLst>
            <a:lnStyleLst>
              <a:ln w="6350" cap="flat" cmpd="sng" algn="ctr">
                <a:solidFill>
                  <a:schemeClr val="phClr"/>
                </a:solidFill>
                <a:prstDash val="solid"/>
                <a:miter lim="800000"/>
              </a:ln>
              <a:ln w="12700" cap="flat" cmpd="sng" algn="ctr">
                <a:solidFill>
                  <a:schemeClr val="phClr"/>
                </a:solidFill>
                <a:prstDash val="solid"/>
                <a:miter lim="800000"/>
              </a:ln>
              <a:ln w="19050" cap="flat" cmpd="sng" algn="ctr">
                <a:solidFill>
                  <a:schemeClr val="phClr"/>
                </a:solidFill>
                <a:prstDash val="solid"/>
                <a:miter lim="800000"/>
              </a:ln>
            </a:lnStyleLst>
            <a:effectStyleLst>
              <a:effectStyle>
                <a:effectLst/>
              </a:effectStyle>
              <a:effectStyle>
                <a:effectLst/>
              </a:effectStyle>
              <a:effectStyle>
                <a:effectLst>
                  <a:outerShdw blurRad="57150" dist="19050" dir="5400000" algn="ctr" rotWithShape="0">
                    <a:srgbClr val="000000">
                      <a:alpha val="63000"/>
                    </a:srgbClr>
                  </a:outerShdw>
                </a:effectLst>
              </a:effectStyle>
            </a:effectStyleLst>
            <a:bgFillStyleLst>
              <a:solidFill>
                <a:schemeClr val="phClr"/>
              </a:solidFill>
              <a:solidFill>
                <a:schemeClr val="phClr">
                  <a:tint val="95000"/>
                  <a:satMod val="170000"/>
                </a:schemeClr>
              </a:solidFill>
              <a:gradFill rotWithShape="1">
                <a:gsLst>
                  <a:gs pos="0">
                    <a:schemeClr val="phClr">
                      <a:tint val="93000"/>
                      <a:satMod val="150000"/>
                      <a:shade val="98000"/>
                      <a:lumMod val="102000"/>
                    </a:schemeClr>
                  </a:gs>
                  <a:gs pos="50000">
                    <a:schemeClr val="phClr">
                      <a:tint val="98000"/>
                      <a:satMod val="130000"/>
                      <a:shade val="90000"/>
                      <a:lumMod val="103000"/>
                    </a:schemeClr>
                  </a:gs>
                  <a:gs pos="100000">
                    <a:schemeClr val="phClr">
                      <a:shade val="63000"/>
                      <a:satMod val="120000"/>
                    </a:schemeClr>
                  </a:gs>
                </a:gsLst>
                <a:lin ang="5400000" scaled="0"/>
              </a:gradFill>
            </a:bgFillStyleLst>
          </a:fmtScheme>
        </a:themeElements>
        <a:objectDefaults/>
        <a:extraClrSchemeLst/>
        <a:extLst>
          <a:ext uri="{05A4C25C-085E-4340-85A3-A5531E510DB2}">
            <thm15:themeFamily xmlns:thm15="http://schemas.microsoft.com/office/thememl/2012/main" name="Office Theme" id="{62F939B6-93AF-4DB8-9C6B-D6C7DFDC589F}" vid="{4A3C46E8-61CC-4603-A589-7422A47A8E4A}"></thm15:themeFamily>
          </a:ext>
        </a:extLst>
      </a:theme>
    </pkg:xmlData>
  </pkg:part>
  <pkg:part pkg:name="/word/settings.xml" pkg:contentType="application/vnd.openxmlformats-officedocument.wordprocessingml.settings+xml">
    <pkg:xmlData>
      <w:settings xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main" xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006" xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:w10="urn:schemas-microsoft-com:office:word" xmlns:w14="http://schemas.microsoft.com/office/word/2010/wordml" xmlns:w15="http://schemas.microsoft.com/office/word/2012/wordml" xmlns:sl="http://schemas.openxmlformats.org/schemaLibrary/2006/main" mc:Ignorable="w14 w15">
        <w:zoom w:percent="110"/>
        <w:bordersDoNotSurroundHeader/>
        <w:bordersDoNotSurroundFooter/>
        <w:defaultTabStop w:val="420"/>
        <w:drawingGridVerticalSpacing w:val="156"/>
        <w:displayHorizontalDrawingGridEvery w:val="0"/>
        <w:displayVerticalDrawingGridEvery w:val="2"/>
        <w:characterSpacingControl w:val="compressPunctuation"/>
        <w:hdrShapeDefaults>
          <o:shapedefaults v:ext="edit" spidmax="2049"/>
        </w:hdrShapeDefaults>
        <w:footnotePr>
          <w:footnote w:id="-1"/>
          <w:footnote w:id="0"/>
        </w:footnotePr>
        <w:endnotePr>
          <w:endnote w:id="-1"/>
          <w:endnote w:id="0"/>
        </w:endnotePr>
        <w:compat>
          <w:spaceForUL/>
          <w:balanceSingleByteDoubleByteWidth/>
          <w:doNotLeaveBackslashAlone/>
          <w:ulTrailSpace/>
          <w:doNotExpandShiftReturn/>
          <w:adjustLineHeightInTable/>
          <w:useFELayout/>
          <w:compatSetting w:name="compatibilityMode" w:uri="http://schemas.microsoft.com/office/word" w:val="15"/>
          <w:compatSetting w:name="overrideTableStyleFontSizeAndJustification" w:uri="http://schemas.microsoft.com/office/word" w:val="1"/>
          <w:compatSetting w:name="enableOpenTypeFeatures" w:uri="http://schemas.microsoft.com/office/word" w:val="1"/>
          <w:compatSetting w:name="doNotFlipMirrorIndents" w:uri="http://schemas.microsoft.com/office/word" w:val="1"/>
          <w:compatSetting w:name="differentiateMultirowTableHeaders" w:uri="http://schemas.microsoft.com/office/word" w:val="1"/>
        </w:compat>
        <w:rsids>
          <w:rsidRoot w:val="00E26655"/>
          <w:rsid w:val="00077F40"/>
          <w:rsid w:val="003B190B"/>
          <w:rsid w:val="00403C8C"/>
          <w:rsid w:val="00495C49"/>
          <w:rsid w:val="00587A70"/>
          <w:rsid w:val="0068088B"/>
          <w:rsid w:val="007F31A9"/>
          <w:rsid w:val="00AF064C"/>
          <w:rsid w:val="00B009AB"/>
          <w:rsid w:val="00B44172"/>
          <w:rsid w:val="00CA27A7"/>
          <w:rsid w:val="00CF4303"/>
          <w:rsid w:val="00E26655"/>
          <w:rsid w:val="00EE2937"/>
          <w:rsid w:val="00F135B6"/>
          <w:rsid w:val="00F92BA5"/>
        </w:rsids>
        <m:mathPr>
          <m:mathFont m:val="Cambria Math"/>
          <m:brkBin m:val="before"/>
          <m:brkBinSub m:val="--"/>
          <m:smallFrac m:val="0"/>
          <m:dispDef/>
          <m:lMargin m:val="0"/>
          <m:rMargin m:val="0"/>
          <m:defJc m:val="centerGroup"/>
          <m:wrapIndent m:val="1440"/>
          <m:intLim m:val="subSup"/>
          <m:naryLim m:val="undOvr"/>
        </m:mathPr>
        <w:themeFontLang w:val="en-US" w:eastAsia="zh-CN"/>
        <w:clrSchemeMapping w:bg1="light1" w:t1="dark1" w:bg2="light2" w:t2="dark2" w:accent1="accent1" w:accent2="accent2" w:accent3="accent3" w:accent4="accent4" w:accent5="accent5" w:accent6="accent6" w:hyperlink="hyperlink" w:followedHyperlink="followedHyperlink"/>
        <w:shapeDefaults>
          <o:shapedefaults v:ext="edit" spidmax="2049"/>
          <o:shapelayout v:ext="edit">
            <o:idmap v:ext="edit" data="1"/>
          </o:shapelayout>
        </w:shapeDefaults>
        <w:decimalSymbol w:val="."/>
        <w:listSeparator w:val=","/>
        <w15:chartTrackingRefBased/>
        <w15:docId w15:val="{601672D0-820B-4787-84B8-3A1057338831}"/>
      </w:settings>
    </pkg:xmlData>
  </pkg:part>
  <pkg:part pkg:name="/word/webSettings.xml" pkg:contentType="application/vnd.openxmlformats-officedocument.wordprocessingml.webSettings+xml">
    <pkg:xmlData>
      <w:webSettings xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main" xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" xmlns:w14="http://schemas.microsoft.com/office/word/2010/wordml" xmlns:w15="http://schemas.microsoft.com/office/word/2012/wordml" mc:Ignorable="w14 w15">
        <w:optimizeForBrowser/>
        <w:allowPNG/>
      </w:webSettings>
    </pkg:xmlData>
  </pkg:part>
  <pkg:part pkg:name="/word/styles.xml" pkg:contentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml">
    <pkg:xmlData>
      <w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main" xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" xmlns:w14="http://schemas.microsoft.com/office/word/2010/wordml" xmlns:w15="http://schemas.microsoft.com/office/word/2012/wordml" mc:Ignorable="w14 w15">
        <w:docDefaults>
          <w:rPrDefault>
            <w:rPr>
              <w:rFonts w:asciiTheme="minorHAnsi" w:eastAsiaTheme="minorEastAsia" w:hAnsiTheme="minorHAnsi" w:cstheme="minorBidi"/>
              <w:kern w:val="2"/>
              <w:sz w:val="21"/>
              <w:szCs w:val="22"/>
              <w:lang w:val="en-US" w:eastAsia="zh-CN" w:bidi="ar-SA"/>
            </w:rPr>
          </w:rPrDefault>
          <w:pPrDefault/>
        </w:docDefaults>
        <w:latentStyles w:defLockedState="0" w:defUIPriority="99" w:defSemiHidden="0" w:defUnhideWhenUsed="0" w:defQFormat="0" w:count="371">
          <w:lsdException w:name="Normal" w:uiPriority="0" w:qFormat="1"/>
          <w:lsdException w:name="heading 1" w:uiPriority="9" w:qFormat="1"/>
          <w:lsdException w:name="heading 2" w:semiHidden="1" w:uiPriority="9" w:unhideWhenUsed="1" w:qFormat="1"/>
          <w:lsdException w:name="heading 3" w:semiHidden="1" w:uiPriority="9" w:unhideWhenUsed="1" w:qFormat="1"/>
          <w:lsdException w:name="heading 4" w:semiHidden="1" w:uiPriority="9" w:unhideWhenUsed="1" w:qFormat="1"/>
          <w:lsdException w:name="heading 5" w:semiHidden="1" w:uiPriority="9" w:unhideWhenUsed="1" w:qFormat="1"/>
          <w:lsdException w:name="heading 6" w:semiHidden="1" w:uiPriority="9" w:unhideWhenUsed="1" w:qFormat="1"/>
          <w:lsdException w:name="heading 7" w:semiHidden="1" w:uiPriority="9" w:unhideWhenUsed="1" w:qFormat="1"/>
          <w:lsdException w:name="heading 8" w:semiHidden="1" w:uiPriority="9" w:unhideWhenUsed="1" w:qFormat="1"/>
          <w:lsdException w:name="heading 9" w:semiHidden="1" w:uiPriority="9" w:unhideWhenUsed="1" w:qFormat="1"/>
          <w:lsdException w:name="index 1" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="index 2" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="index 3" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="index 4" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="index 5" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="index 6" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="index 7" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="index 8" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="index 9" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="toc 1" w:semiHidden="1" w:uiPriority="39" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="toc 2" w:semiHidden="1" w:uiPriority="39" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="toc 3" w:semiHidden="1" w:uiPriority="39" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="toc 4" w:semiHidden="1" w:uiPriority="39" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="toc 5" w:semiHidden="1" w:uiPriority="39" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="toc 6" w:semiHidden="1" w:uiPriority="39" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="toc 7" w:semiHidden="1" w:uiPriority="39" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="toc 8" w:semiHidden="1" w:uiPriority="39" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="toc 9" w:semiHidden="1" w:uiPriority="39" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Normal Indent" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="footnote text" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="annotation text" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="header" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="footer" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="index heading" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="caption" w:semiHidden="1" w:uiPriority="35" w:unhideWhenUsed="1" w:qFormat="1"/>
          <w:lsdException w:name="table of figures" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="envelope address" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="envelope return" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="footnote reference" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="annotation reference" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="line number" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="page number" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="endnote reference" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="endnote text" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="table of authorities" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="macro" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="toa heading" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List Bullet" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List Number" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List 2" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List 3" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List 4" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List 5" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List Bullet 2" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List Bullet 3" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List Bullet 4" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List Bullet 5" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List Number 2" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List Number 3" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List Number 4" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List Number 5" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Title" w:uiPriority="10" w:qFormat="1"/>
          <w:lsdException w:name="Closing" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Signature" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Default Paragraph Font" w:semiHidden="1" w:uiPriority="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Body Text" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Body Text Indent" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List Continue" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List Continue 2" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List Continue 3" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List Continue 4" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="List Continue 5" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Message Header" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Subtitle" w:uiPriority="11" w:qFormat="1"/>
          <w:lsdException w:name="Salutation" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Date" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Body Text First Indent" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Body Text First Indent 2" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Note Heading" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Body Text 2" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Body Text 3" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Body Text Indent 2" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Body Text Indent 3" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Block Text" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Hyperlink" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="FollowedHyperlink" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Strong" w:uiPriority="22" w:qFormat="1"/>
          <w:lsdException w:name="Emphasis" w:uiPriority="20" w:qFormat="1"/>
          <w:lsdException w:name="Document Map" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Plain Text" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="E-mail Signature" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="HTML Top of Form" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="HTML Bottom of Form" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Normal (Web)" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="HTML Acronym" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="HTML Address" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="HTML Cite" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="HTML Code" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="HTML Definition" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="HTML Keyboard" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="HTML Preformatted" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="HTML Sample" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="HTML Typewriter" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="HTML Variable" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Normal Table" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="annotation subject" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="No List" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Outline List 1" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Outline List 2" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Outline List 3" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Simple 1" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Simple 2" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Simple 3" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Classic 1" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Classic 2" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Classic 3" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Classic 4" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Colorful 1" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Colorful 2" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Colorful 3" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Columns 1" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Columns 2" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Columns 3" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Columns 4" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Columns 5" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Grid 1" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Grid 2" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Grid 3" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Grid 4" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Grid 5" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Grid 6" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Grid 7" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Grid 8" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table List 1" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table List 2" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table List 3" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table List 4" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table List 5" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table List 6" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table List 7" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table List 8" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table 3D effects 1" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table 3D effects 2" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table 3D effects 3" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Contemporary" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Elegant" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Professional" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Subtle 1" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Subtle 2" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Web 1" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Web 2" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Web 3" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Balloon Text" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Table Grid" w:uiPriority="39"/>
          <w:lsdException w:name="Table Theme" w:semiHidden="1" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="Placeholder Text" w:semiHidden="1"/>
          <w:lsdException w:name="No Spacing" w:uiPriority="1" w:qFormat="1"/>
          <w:lsdException w:name="Light Shading" w:uiPriority="60"/>
          <w:lsdException w:name="Light List" w:uiPriority="61"/>
          <w:lsdException w:name="Light Grid" w:uiPriority="62"/>
          <w:lsdException w:name="Medium Shading 1" w:uiPriority="63"/>
          <w:lsdException w:name="Medium Shading 2" w:uiPriority="64"/>
          <w:lsdException w:name="Medium List 1" w:uiPriority="65"/>
          <w:lsdException w:name="Medium List 2" w:uiPriority="66"/>
          <w:lsdException w:name="Medium Grid 1" w:uiPriority="67"/>
          <w:lsdException w:name="Medium Grid 2" w:uiPriority="68"/>
          <w:lsdException w:name="Medium Grid 3" w:uiPriority="69"/>
          <w:lsdException w:name="Dark List" w:uiPriority="70"/>
          <w:lsdException w:name="Colorful Shading" w:uiPriority="71"/>
          <w:lsdException w:name="Colorful List" w:uiPriority="72"/>
          <w:lsdException w:name="Colorful Grid" w:uiPriority="73"/>
          <w:lsdException w:name="Light Shading Accent 1" w:uiPriority="60"/>
          <w:lsdException w:name="Light List Accent 1" w:uiPriority="61"/>
          <w:lsdException w:name="Light Grid Accent 1" w:uiPriority="62"/>
          <w:lsdException w:name="Medium Shading 1 Accent 1" w:uiPriority="63"/>
          <w:lsdException w:name="Medium Shading 2 Accent 1" w:uiPriority="64"/>
          <w:lsdException w:name="Medium List 1 Accent 1" w:uiPriority="65"/>
          <w:lsdException w:name="Revision" w:semiHidden="1"/>
          <w:lsdException w:name="List Paragraph" w:uiPriority="34" w:qFormat="1"/>
          <w:lsdException w:name="Quote" w:uiPriority="29" w:qFormat="1"/>
          <w:lsdException w:name="Intense Quote" w:uiPriority="30" w:qFormat="1"/>
          <w:lsdException w:name="Medium List 2 Accent 1" w:uiPriority="66"/>
          <w:lsdException w:name="Medium Grid 1 Accent 1" w:uiPriority="67"/>
          <w:lsdException w:name="Medium Grid 2 Accent 1" w:uiPriority="68"/>
          <w:lsdException w:name="Medium Grid 3 Accent 1" w:uiPriority="69"/>
          <w:lsdException w:name="Dark List Accent 1" w:uiPriority="70"/>
          <w:lsdException w:name="Colorful Shading Accent 1" w:uiPriority="71"/>
          <w:lsdException w:name="Colorful List Accent 1" w:uiPriority="72"/>
          <w:lsdException w:name="Colorful Grid Accent 1" w:uiPriority="73"/>
          <w:lsdException w:name="Light Shading Accent 2" w:uiPriority="60"/>
          <w:lsdException w:name="Light List Accent 2" w:uiPriority="61"/>
          <w:lsdException w:name="Light Grid Accent 2" w:uiPriority="62"/>
          <w:lsdException w:name="Medium Shading 1 Accent 2" w:uiPriority="63"/>
          <w:lsdException w:name="Medium Shading 2 Accent 2" w:uiPriority="64"/>
          <w:lsdException w:name="Medium List 1 Accent 2" w:uiPriority="65"/>
          <w:lsdException w:name="Medium List 2 Accent 2" w:uiPriority="66"/>
          <w:lsdException w:name="Medium Grid 1 Accent 2" w:uiPriority="67"/>
          <w:lsdException w:name="Medium Grid 2 Accent 2" w:uiPriority="68"/>
          <w:lsdException w:name="Medium Grid 3 Accent 2" w:uiPriority="69"/>
          <w:lsdException w:name="Dark List Accent 2" w:uiPriority="70"/>
          <w:lsdException w:name="Colorful Shading Accent 2" w:uiPriority="71"/>
          <w:lsdException w:name="Colorful List Accent 2" w:uiPriority="72"/>
          <w:lsdException w:name="Colorful Grid Accent 2" w:uiPriority="73"/>
          <w:lsdException w:name="Light Shading Accent 3" w:uiPriority="60"/>
          <w:lsdException w:name="Light List Accent 3" w:uiPriority="61"/>
          <w:lsdException w:name="Light Grid Accent 3" w:uiPriority="62"/>
          <w:lsdException w:name="Medium Shading 1 Accent 3" w:uiPriority="63"/>
          <w:lsdException w:name="Medium Shading 2 Accent 3" w:uiPriority="64"/>
          <w:lsdException w:name="Medium List 1 Accent 3" w:uiPriority="65"/>
          <w:lsdException w:name="Medium List 2 Accent 3" w:uiPriority="66"/>
          <w:lsdException w:name="Medium Grid 1 Accent 3" w:uiPriority="67"/>
          <w:lsdException w:name="Medium Grid 2 Accent 3" w:uiPriority="68"/>
          <w:lsdException w:name="Medium Grid 3 Accent 3" w:uiPriority="69"/>
          <w:lsdException w:name="Dark List Accent 3" w:uiPriority="70"/>
          <w:lsdException w:name="Colorful Shading Accent 3" w:uiPriority="71"/>
          <w:lsdException w:name="Colorful List Accent 3" w:uiPriority="72"/>
          <w:lsdException w:name="Colorful Grid Accent 3" w:uiPriority="73"/>
          <w:lsdException w:name="Light Shading Accent 4" w:uiPriority="60"/>
          <w:lsdException w:name="Light List Accent 4" w:uiPriority="61"/>
          <w:lsdException w:name="Light Grid Accent 4" w:uiPriority="62"/>
          <w:lsdException w:name="Medium Shading 1 Accent 4" w:uiPriority="63"/>
          <w:lsdException w:name="Medium Shading 2 Accent 4" w:uiPriority="64"/>
          <w:lsdException w:name="Medium List 1 Accent 4" w:uiPriority="65"/>
          <w:lsdException w:name="Medium List 2 Accent 4" w:uiPriority="66"/>
          <w:lsdException w:name="Medium Grid 1 Accent 4" w:uiPriority="67"/>
          <w:lsdException w:name="Medium Grid 2 Accent 4" w:uiPriority="68"/>
          <w:lsdException w:name="Medium Grid 3 Accent 4" w:uiPriority="69"/>
          <w:lsdException w:name="Dark List Accent 4" w:uiPriority="70"/>
          <w:lsdException w:name="Colorful Shading Accent 4" w:uiPriority="71"/>
          <w:lsdException w:name="Colorful List Accent 4" w:uiPriority="72"/>
          <w:lsdException w:name="Colorful Grid Accent 4" w:uiPriority="73"/>
          <w:lsdException w:name="Light Shading Accent 5" w:uiPriority="60"/>
          <w:lsdException w:name="Light List Accent 5" w:uiPriority="61"/>
          <w:lsdException w:name="Light Grid Accent 5" w:uiPriority="62"/>
          <w:lsdException w:name="Medium Shading 1 Accent 5" w:uiPriority="63"/>
          <w:lsdException w:name="Medium Shading 2 Accent 5" w:uiPriority="64"/>
          <w:lsdException w:name="Medium List 1 Accent 5" w:uiPriority="65"/>
          <w:lsdException w:name="Medium List 2 Accent 5" w:uiPriority="66"/>
          <w:lsdException w:name="Medium Grid 1 Accent 5" w:uiPriority="67"/>
          <w:lsdException w:name="Medium Grid 2 Accent 5" w:uiPriority="68"/>
          <w:lsdException w:name="Medium Grid 3 Accent 5" w:uiPriority="69"/>
          <w:lsdException w:name="Dark List Accent 5" w:uiPriority="70"/>
          <w:lsdException w:name="Colorful Shading Accent 5" w:uiPriority="71"/>
          <w:lsdException w:name="Colorful List Accent 5" w:uiPriority="72"/>
          <w:lsdException w:name="Colorful Grid Accent 5" w:uiPriority="73"/>
          <w:lsdException w:name="Light Shading Accent 6" w:uiPriority="60"/>
          <w:lsdException w:name="Light List Accent 6" w:uiPriority="61"/>
          <w:lsdException w:name="Light Grid Accent 6" w:uiPriority="62"/>
          <w:lsdException w:name="Medium Shading 1 Accent 6" w:uiPriority="63"/>
          <w:lsdException w:name="Medium Shading 2 Accent 6" w:uiPriority="64"/>
          <w:lsdException w:name="Medium List 1 Accent 6" w:uiPriority="65"/>
          <w:lsdException w:name="Medium List 2 Accent 6" w:uiPriority="66"/>
          <w:lsdException w:name="Medium Grid 1 Accent 6" w:uiPriority="67"/>
          <w:lsdException w:name="Medium Grid 2 Accent 6" w:uiPriority="68"/>
          <w:lsdException w:name="Medium Grid 3 Accent 6" w:uiPriority="69"/>
          <w:lsdException w:name="Dark List Accent 6" w:uiPriority="70"/>
          <w:lsdException w:name="Colorful Shading Accent 6" w:uiPriority="71"/>
          <w:lsdException w:name="Colorful List Accent 6" w:uiPriority="72"/>
          <w:lsdException w:name="Colorful Grid Accent 6" w:uiPriority="73"/>
          <w:lsdException w:name="Subtle Emphasis" w:uiPriority="19" w:qFormat="1"/>
          <w:lsdException w:name="Intense Emphasis" w:uiPriority="21" w:qFormat="1"/>
          <w:lsdException w:name="Subtle Reference" w:uiPriority="31" w:qFormat="1"/>
          <w:lsdException w:name="Intense Reference" w:uiPriority="32" w:qFormat="1"/>
          <w:lsdException w:name="Book Title" w:uiPriority="33" w:qFormat="1"/>
          <w:lsdException w:name="Bibliography" w:semiHidden="1" w:uiPriority="37" w:unhideWhenUsed="1"/>
          <w:lsdException w:name="TOC Heading" w:semiHidden="1" w:uiPriority="39" w:unhideWhenUsed="1" w:qFormat="1"/>
          <w:lsdException w:name="Plain Table 1" w:uiPriority="41"/>
          <w:lsdException w:name="Plain Table 2" w:uiPriority="42"/>
          <w:lsdException w:name="Plain Table 3" w:uiPriority="43"/>
          <w:lsdException w:name="Plain Table 4" w:uiPriority="44"/>
          <w:lsdException w:name="Plain Table 5" w:uiPriority="45"/>
          <w:lsdException w:name="Grid Table Light" w:uiPriority="40"/>
          <w:lsdException w:name="Grid Table 1 Light" w:uiPriority="46"/>
          <w:lsdException w:name="Grid Table 2" w:uiPriority="47"/>
          <w:lsdException w:name="Grid Table 3" w:uiPriority="48"/>
          <w:lsdException w:name="Grid Table 4" w:uiPriority="49"/>
          <w:lsdException w:name="Grid Table 5 Dark" w:uiPriority="50"/>
          <w:lsdException w:name="Grid Table 6 Colorful" w:uiPriority="51"/>
          <w:lsdException w:name="Grid Table 7 Colorful" w:uiPriority="52"/>
          <w:lsdException w:name="Grid Table 1 Light Accent 1" w:uiPriority="46"/>
          <w:lsdException w:name="Grid Table 2 Accent 1" w:uiPriority="47"/>
          <w:lsdException w:name="Grid Table 3 Accent 1" w:uiPriority="48"/>
          <w:lsdException w:name="Grid Table 4 Accent 1" w:uiPriority="49"/>
          <w:lsdException w:name="Grid Table 5 Dark Accent 1" w:uiPriority="50"/>
          <w:lsdException w:name="Grid Table 6 Colorful Accent 1" w:uiPriority="51"/>
          <w:lsdException w:name="Grid Table 7 Colorful Accent 1" w:uiPriority="52"/>
          <w:lsdException w:name="Grid Table 1 Light Accent 2" w:uiPriority="46"/>
          <w:lsdException w:name="Grid Table 2 Accent 2" w:uiPriority="47"/>
          <w:lsdException w:name="Grid Table 3 Accent 2" w:uiPriority="48"/>
          <w:lsdException w:name="Grid Table 4 Accent 2" w:uiPriority="49"/>
          <w:lsdException w:name="Grid Table 5 Dark Accent 2" w:uiPriority="50"/>
          <w:lsdException w:name="Grid Table 6 Colorful Accent 2" w:uiPriority="51"/>
          <w:lsdException w:name="Grid Table 7 Colorful Accent 2" w:uiPriority="52"/>
          <w:lsdException w:name="Grid Table 1 Light Accent 3" w:uiPriority="46"/>
          <w:lsdException w:name="Grid Table 2 Accent 3" w:uiPriority="47"/>
          <w:lsdException w:name="Grid Table 3 Accent 3" w:uiPriority="48"/>
          <w:lsdException w:name="Grid Table 4 Accent 3" w:uiPriority="49"/>
          <w:lsdException w:name="Grid Table 5 Dark Accent 3" w:uiPriority="50"/>
          <w:lsdException w:name="Grid Table 6 Colorful Accent 3" w:uiPriority="51"/>
          <w:lsdException w:name="Grid Table 7 Colorful Accent 3" w:uiPriority="52"/>
          <w:lsdException w:name="Grid Table 1 Light Accent 4" w:uiPriority="46"/>
          <w:lsdException w:name="Grid Table 2 Accent 4" w:uiPriority="47"/>
          <w:lsdException w:name="Grid Table 3 Accent 4" w:uiPriority="48"/>
          <w:lsdException w:name="Grid Table 4 Accent 4" w:uiPriority="49"/>
          <w:lsdException w:name="Grid Table 5 Dark Accent 4" w:uiPriority="50"/>
          <w:lsdException w:name="Grid Table 6 Colorful Accent 4" w:uiPriority="51"/>
          <w:lsdException w:name="Grid Table 7 Colorful Accent 4" w:uiPriority="52"/>
          <w:lsdException w:name="Grid Table 1 Light Accent 5" w:uiPriority="46"/>
          <w:lsdException w:name="Grid Table 2 Accent 5" w:uiPriority="47"/>
          <w:lsdException w:name="Grid Table 3 Accent 5" w:uiPriority="48"/>
          <w:lsdException w:name="Grid Table 4 Accent 5" w:uiPriority="49"/>
          <w:lsdException w:name="Grid Table 5 Dark Accent 5" w:uiPriority="50"/>
          <w:lsdException w:name="Grid Table 6 Colorful Accent 5" w:uiPriority="51"/>
          <w:lsdException w:name="Grid Table 7 Colorful Accent 5" w:uiPriority="52"/>
          <w:lsdException w:name="Grid Table 1 Light Accent 6" w:uiPriority="46"/>
          <w:lsdException w:name="Grid Table 2 Accent 6" w:uiPriority="47"/>
          <w:lsdException w:name="Grid Table 3 Accent 6" w:uiPriority="48"/>
          <w:lsdException w:name="Grid Table 4 Accent 6" w:uiPriority="49"/>
          <w:lsdException w:name="Grid Table 5 Dark Accent 6" w:uiPriority="50"/>
          <w:lsdException w:name="Grid Table 6 Colorful Accent 6" w:uiPriority="51"/>
          <w:lsdException w:name="Grid Table 7 Colorful Accent 6" w:uiPriority="52"/>
          <w:lsdException w:name="List Table 1 Light" w:uiPriority="46"/>
          <w:lsdException w:name="List Table 2" w:uiPriority="47"/>
          <w:lsdException w:name="List Table 3" w:uiPriority="48"/>
          <w:lsdException w:name="List Table 4" w:uiPriority="49"/>
          <w:lsdException w:name="List Table 5 Dark" w:uiPriority="50"/>
          <w:lsdException w:name="List Table 6 Colorful" w:uiPriority="51"/>
          <w:lsdException w:name="List Table 7 Colorful" w:uiPriority="52"/>
          <w:lsdException w:name="List Table 1 Light Accent 1" w:uiPriority="46"/>
          <w:lsdException w:name="List Table 2 Accent 1" w:uiPriority="47"/>
          <w:lsdException w:name="List Table 3 Accent 1" w:uiPriority="48"/>
          <w:lsdException w:name="List Table 4 Accent 1" w:uiPriority="49"/>
          <w:lsdException w:name="List Table 5 Dark Accent 1" w:uiPriority="50"/>
          <w:lsdException w:name="List Table 6 Colorful Accent 1" w:uiPriority="51"/>
          <w:lsdException w:name="List Table 7 Colorful Accent 1" w:uiPriority="52"/>
          <w:lsdException w:name="List Table 1 Light Accent 2" w:uiPriority="46"/>
          <w:lsdException w:name="List Table 2 Accent 2" w:uiPriority="47"/>
          <w:lsdException w:name="List Table 3 Accent 2" w:uiPriority="48"/>
          <w:lsdException w:name="List Table 4 Accent 2" w:uiPriority="49"/>
          <w:lsdException w:name="List Table 5 Dark Accent 2" w:uiPriority="50"/>
          <w:lsdException w:name="List Table 6 Colorful Accent 2" w:uiPriority="51"/>
          <w:lsdException w:name="List Table 7 Colorful Accent 2" w:uiPriority="52"/>
          <w:lsdException w:name="List Table 1 Light Accent 3" w:uiPriority="46"/>
          <w:lsdException w:name="List Table 2 Accent 3" w:uiPriority="47"/>
          <w:lsdException w:name="List Table 3 Accent 3" w:uiPriority="48"/>
          <w:lsdException w:name="List Table 4 Accent 3" w:uiPriority="49"/>
          <w:lsdException w:name="List Table 5 Dark Accent 3" w:uiPriority="50"/>
          <w:lsdException w:name="List Table 6 Colorful Accent 3" w:uiPriority="51"/>
          <w:lsdException w:name="List Table 7 Colorful Accent 3" w:uiPriority="52"/>
          <w:lsdException w:name="List Table 1 Light Accent 4" w:uiPriority="46"/>
          <w:lsdException w:name="List Table 2 Accent 4" w:uiPriority="47"/>
          <w:lsdException w:name="List Table 3 Accent 4" w:uiPriority="48"/>
          <w:lsdException w:name="List Table 4 Accent 4" w:uiPriority="49"/>
          <w:lsdException w:name="List Table 5 Dark Accent 4" w:uiPriority="50"/>
          <w:lsdException w:name="List Table 6 Colorful Accent 4" w:uiPriority="51"/>
          <w:lsdException w:name="List Table 7 Colorful Accent 4" w:uiPriority="52"/>
          <w:lsdException w:name="List Table 1 Light Accent 5" w:uiPriority="46"/>
          <w:lsdException w:name="List Table 2 Accent 5" w:uiPriority="47"/>
          <w:lsdException w:name="List Table 3 Accent 5" w:uiPriority="48"/>
          <w:lsdException w:name="List Table 4 Accent 5" w:uiPriority="49"/>
          <w:lsdException w:name="List Table 5 Dark Accent 5" w:uiPriority="50"/>
          <w:lsdException w:name="List Table 6 Colorful Accent 5" w:uiPriority="51"/>
          <w:lsdException w:name="List Table 7 Colorful Accent 5" w:uiPriority="52"/>
          <w:lsdException w:name="List Table 1 Light Accent 6" w:uiPriority="46"/>
          <w:lsdException w:name="List Table 2 Accent 6" w:uiPriority="47"/>
          <w:lsdException w:name="List Table 3 Accent 6" w:uiPriority="48"/>
          <w:lsdException w:name="List Table 4 Accent 6" w:uiPriority="49"/>
          <w:lsdException w:name="List Table 5 Dark Accent 6" w:uiPriority="50"/>
          <w:lsdException w:name="List Table 6 Colorful Accent 6" w:uiPriority="51"/>
          <w:lsdException w:name="List Table 7 Colorful Accent 6" w:uiPriority="52"/>
        </w:latentStyles>
        <w:style w:type="paragraph" w:default="1" w:styleId="a">
          <w:name w:val="Normal"/>
          <w:qFormat/>
          <w:pPr>
            <w:widowControl w:val="0"/>
            <w:jc w:val="both"/>
          </w:pPr>
        </w:style>
        <w:style w:type="paragraph" w:styleId="1">
          <w:name w:val="heading 1"/>
          <w:basedOn w:val="a"/>
          <w:next w:val="a"/>
          <w:link w:val="1Char"/>
          <w:uiPriority w:val="9"/>
          <w:qFormat/>
          <w:rsid w:val="003B190B"/>
          <w:pPr>
            <w:keepNext/>
            <w:keepLines/>
            <w:spacing w:before="340" w:after="330" w:line="578" w:lineRule="auto"/>
            <w:outlineLvl w:val="0"/>
          </w:pPr>
          <w:rPr>
            <w:b/>
            <w:bCs/>
            <w:kern w:val="44"/>
            <w:sz w:val="44"/>
            <w:szCs w:val="44"/>
          </w:rPr>
        </w:style>
        <w:style w:type="paragraph" w:styleId="2">
          <w:name w:val="heading 2"/>
          <w:basedOn w:val="a"/>
          <w:next w:val="a"/>
          <w:link w:val="2Char"/>
          <w:uiPriority w:val="9"/>
          <w:unhideWhenUsed/>
          <w:qFormat/>
          <w:rsid w:val="003B190B"/>
          <w:pPr>
            <w:keepNext/>
            <w:keepLines/>
            <w:spacing w:before="260" w:after="260" w:line="416" w:lineRule="auto"/>
            <w:outlineLvl w:val="1"/>
          </w:pPr>
          <w:rPr>
            <w:rFonts w:asciiTheme="majorHAnsi" w:eastAsiaTheme="majorEastAsia" w:hAnsiTheme="majorHAnsi" w:cstheme="majorBidi"/>
            <w:b/>
            <w:bCs/>
            <w:sz w:val="32"/>
            <w:szCs w:val="32"/>
          </w:rPr>
        </w:style>
        <w:style w:type="paragraph" w:styleId="3">
          <w:name w:val="heading 3"/>
          <w:basedOn w:val="a"/>
          <w:next w:val="a"/>
          <w:link w:val="3Char"/>
          <w:uiPriority w:val="9"/>
          <w:unhideWhenUsed/>
          <w:qFormat/>
          <w:rsid w:val="003B190B"/>
          <w:pPr>
            <w:keepNext/>
            <w:keepLines/>
            <w:spacing w:before="260" w:after="260" w:line="416" w:lineRule="auto"/>
            <w:outlineLvl w:val="2"/>
          </w:pPr>
          <w:rPr>
            <w:b/>
            <w:bCs/>
            <w:sz w:val="32"/>
            <w:szCs w:val="32"/>
          </w:rPr>
        </w:style>
        <w:style w:type="paragraph" w:styleId="4">
          <w:name w:val="heading 4"/>
          <w:basedOn w:val="a"/>
          <w:next w:val="a"/>
          <w:link w:val="4Char"/>
          <w:uiPriority w:val="9"/>
          <w:unhideWhenUsed/>
          <w:qFormat/>
          <w:rsid w:val="003B190B"/>
          <w:pPr>
            <w:keepNext/>
            <w:keepLines/>
            <w:spacing w:before="280" w:after="290" w:line="376" w:lineRule="auto"/>
            <w:outlineLvl w:val="3"/>
          </w:pPr>
          <w:rPr>
            <w:rFonts w:asciiTheme="majorHAnsi" w:eastAsiaTheme="majorEastAsia" w:hAnsiTheme="majorHAnsi" w:cstheme="majorBidi"/>
            <w:b/>
            <w:bCs/>
            <w:sz w:val="28"/>
            <w:szCs w:val="28"/>
          </w:rPr>
        </w:style>
        <w:style w:type="paragraph" w:styleId="5">
          <w:name w:val="heading 5"/>
          <w:basedOn w:val="a"/>
          <w:next w:val="a"/>
          <w:link w:val="5Char"/>
          <w:uiPriority w:val="9"/>
          <w:unhideWhenUsed/>
          <w:qFormat/>
          <w:rsid w:val="003B190B"/>
          <w:pPr>
            <w:keepNext/>
            <w:keepLines/>
            <w:spacing w:before="280" w:after="290" w:line="376" w:lineRule="auto"/>
            <w:outlineLvl w:val="4"/>
          </w:pPr>
          <w:rPr>
            <w:b/>
            <w:bCs/>
            <w:sz w:val="28"/>
            <w:szCs w:val="28"/>
          </w:rPr>
        </w:style>
        <w:style w:type="paragraph" w:styleId="6">
          <w:name w:val="heading 6"/>
          <w:basedOn w:val="a"/>
          <w:next w:val="a"/>
          <w:link w:val="6Char"/>
          <w:uiPriority w:val="9"/>
          <w:unhideWhenUsed/>
          <w:qFormat/>
          <w:rsid w:val="003B190B"/>
          <w:pPr>
            <w:keepNext/>
            <w:keepLines/>
            <w:spacing w:before="240" w:after="64" w:line="320" w:lineRule="auto"/>
            <w:outlineLvl w:val="5"/>
          </w:pPr>
          <w:rPr>
            <w:rFonts w:asciiTheme="majorHAnsi" w:eastAsiaTheme="majorEastAsia" w:hAnsiTheme="majorHAnsi" w:cstheme="majorBidi"/>
            <w:b/>
            <w:bCs/>
            <w:sz w:val="24"/>
            <w:szCs w:val="24"/>
          </w:rPr>
        </w:style>
        <w:style w:type="paragraph" w:styleId="7">
          <w:name w:val="heading 7"/>
          <w:basedOn w:val="a"/>
          <w:next w:val="a"/>
          <w:link w:val="7Char"/>
          <w:uiPriority w:val="9"/>
          <w:unhideWhenUsed/>
          <w:qFormat/>
          <w:rsid w:val="003B190B"/>
          <w:pPr>
            <w:keepNext/>
            <w:keepLines/>
            <w:spacing w:before="240" w:after="64" w:line="320" w:lineRule="auto"/>
            <w:outlineLvl w:val="6"/>
          </w:pPr>
          <w:rPr>
            <w:b/>
            <w:bCs/>
            <w:sz w:val="24"/>
            <w:szCs w:val="24"/>
          </w:rPr>
        </w:style>
        <w:style w:type="paragraph" w:styleId="8">
          <w:name w:val="heading 8"/>
          <w:basedOn w:val="a"/>
          <w:next w:val="a"/>
          <w:link w:val="8Char"/>
          <w:uiPriority w:val="9"/>
          <w:unhideWhenUsed/>
          <w:qFormat/>
          <w:rsid w:val="003B190B"/>
          <w:pPr>
            <w:keepNext/>
            <w:keepLines/>
            <w:spacing w:before="240" w:after="64" w:line="320" w:lineRule="auto"/>
            <w:outlineLvl w:val="7"/>
          </w:pPr>
          <w:rPr>
            <w:rFonts w:asciiTheme="majorHAnsi" w:eastAsiaTheme="majorEastAsia" w:hAnsiTheme="majorHAnsi" w:cstheme="majorBidi"/>
            <w:sz w:val="24"/>
            <w:szCs w:val="24"/>
          </w:rPr>
        </w:style>
        <w:style w:type="paragraph" w:styleId="9">
          <w:name w:val="heading 9"/>
          <w:basedOn w:val="a"/>
          <w:next w:val="a"/>
          <w:link w:val="9Char"/>
          <w:uiPriority w:val="9"/>
          <w:unhideWhenUsed/>
          <w:qFormat/>
          <w:rsid w:val="003B190B"/>
          <w:pPr>
            <w:keepNext/>
            <w:keepLines/>
            <w:spacing w:before="240" w:after="64" w:line="320" w:lineRule="auto"/>
            <w:outlineLvl w:val="8"/>
          </w:pPr>
          <w:rPr>
            <w:rFonts w:asciiTheme="majorHAnsi" w:eastAsiaTheme="majorEastAsia" w:hAnsiTheme="majorHAnsi" w:cstheme="majorBidi"/>
            <w:szCs w:val="21"/>
          </w:rPr>
        </w:style>
        <w:style w:type="character" w:default="1" w:styleId="a0">
          <w:name w:val="Default Paragraph Font"/>
          <w:uiPriority w:val="1"/>
          <w:semiHidden/>
          <w:unhideWhenUsed/>
        </w:style>
        <w:style w:type="table" w:default="1" w:styleId="a1">
          <w:name w:val="Normal Table"/>
          <w:uiPriority w:val="99"/>
          <w:semiHidden/>
          <w:unhideWhenUsed/>
          <w:tblPr>
            <w:tblInd w:w="0" w:type="dxa"/>
            <w:tblCellMar>
              <w:top w:w="0" w:type="dxa"/>
              <w:left w:w="108" w:type="dxa"/>
              <w:bottom w:w="0" w:type="dxa"/>
              <w:right w:w="108" w:type="dxa"/>
            </w:tblCellMar>
          </w:tblPr>
        </w:style>
        <w:style w:type="numbering" w:default="1" w:styleId="a2">
          <w:name w:val="No List"/>
          <w:uiPriority w:val="99"/>
          <w:semiHidden/>
          <w:unhideWhenUsed/>
        </w:style>
        <w:style w:type="paragraph" w:styleId="a3">
          <w:name w:val="List Paragraph"/>
          <w:basedOn w:val="a"/>
          <w:uiPriority w:val="34"/>
          <w:qFormat/>
          <w:rsid w:val="003B190B"/>
          <w:pPr>
            <w:ind w:firstLineChars="200" w:firstLine="420"/>
          </w:pPr>
        </w:style>
        <w:style w:type="character" w:customStyle="1" w:styleId="1Char">
          <w:name w:val="标题 1 Char"/>
          <w:basedOn w:val="a0"/>
          <w:link w:val="1"/>
          <w:uiPriority w:val="9"/>
          <w:rsid w:val="003B190B"/>
          <w:rPr>
            <w:b/>
            <w:bCs/>
            <w:kern w:val="44"/>
            <w:sz w:val="44"/>
            <w:szCs w:val="44"/>
          </w:rPr>
        </w:style>
        <w:style w:type="character" w:customStyle="1" w:styleId="2Char">
          <w:name w:val="标题 2 Char"/>
          <w:basedOn w:val="a0"/>
          <w:link w:val="2"/>
          <w:uiPriority w:val="9"/>
          <w:rsid w:val="003B190B"/>
          <w:rPr>
            <w:rFonts w:asciiTheme="majorHAnsi" w:eastAsiaTheme="majorEastAsia" w:hAnsiTheme="majorHAnsi" w:cstheme="majorBidi"/>
            <w:b/>
            <w:bCs/>
            <w:sz w:val="32"/>
            <w:szCs w:val="32"/>
          </w:rPr>
        </w:style>
        <w:style w:type="character" w:customStyle="1" w:styleId="3Char">
          <w:name w:val="标题 3 Char"/>
          <w:basedOn w:val="a0"/>
          <w:link w:val="3"/>
          <w:uiPriority w:val="9"/>
          <w:rsid w:val="003B190B"/>
          <w:rPr>
            <w:b/>
            <w:bCs/>
            <w:sz w:val="32"/>
            <w:szCs w:val="32"/>
          </w:rPr>
        </w:style>
        <w:style w:type="character" w:customStyle="1" w:styleId="4Char">
          <w:name w:val="标题 4 Char"/>
          <w:basedOn w:val="a0"/>
          <w:link w:val="4"/>
          <w:uiPriority w:val="9"/>
          <w:rsid w:val="003B190B"/>
          <w:rPr>
            <w:rFonts w:asciiTheme="majorHAnsi" w:eastAsiaTheme="majorEastAsia" w:hAnsiTheme="majorHAnsi" w:cstheme="majorBidi"/>
            <w:b/>
            <w:bCs/>
            <w:sz w:val="28"/>
            <w:szCs w:val="28"/>
          </w:rPr>
        </w:style>
        <w:style w:type="character" w:customStyle="1" w:styleId="5Char">
          <w:name w:val="标题 5 Char"/>
          <w:basedOn w:val="a0"/>
          <w:link w:val="5"/>
          <w:uiPriority w:val="9"/>
          <w:rsid w:val="003B190B"/>
          <w:rPr>
            <w:b/>
            <w:bCs/>
            <w:sz w:val="28"/>
            <w:szCs w:val="28"/>
          </w:rPr>
        </w:style>
        <w:style w:type="character" w:customStyle="1" w:styleId="6Char">
          <w:name w:val="标题 6 Char"/>
          <w:basedOn w:val="a0"/>
          <w:link w:val="6"/>
          <w:uiPriority w:val="9"/>
          <w:rsid w:val="003B190B"/>
          <w:rPr>
            <w:rFonts w:asciiTheme="majorHAnsi" w:eastAsiaTheme="majorEastAsia" w:hAnsiTheme="majorHAnsi" w:cstheme="majorBidi"/>
            <w:b/>
            <w:bCs/>
            <w:sz w:val="24"/>
            <w:szCs w:val="24"/>
          </w:rPr>
        </w:style>
        <w:style w:type="character" w:customStyle="1" w:styleId="7Char">
          <w:name w:val="标题 7 Char"/>
          <w:basedOn w:val="a0"/>
          <w:link w:val="7"/>
          <w:uiPriority w:val="9"/>
          <w:rsid w:val="003B190B"/>
          <w:rPr>
            <w:b/>
            <w:bCs/>
            <w:sz w:val="24"/>
            <w:szCs w:val="24"/>
          </w:rPr>
        </w:style>
        <w:style w:type="character" w:customStyle="1" w:styleId="8Char">
          <w:name w:val="标题 8 Char"/>
          <w:basedOn w:val="a0"/>
          <w:link w:val="8"/>
          <w:uiPriority w:val="9"/>
          <w:rsid w:val="003B190B"/>
          <w:rPr>
            <w:rFonts w:asciiTheme="majorHAnsi" w:eastAsiaTheme="majorEastAsia" w:hAnsiTheme="majorHAnsi" w:cstheme="majorBidi"/>
            <w:sz w:val="24"/>
            <w:szCs w:val="24"/>
          </w:rPr>
        </w:style>
        <w:style w:type="character" w:customStyle="1" w:styleId="9Char">
          <w:name w:val="标题 9 Char"/>
          <w:basedOn w:val="a0"/>
          <w:link w:val="9"/>
          <w:uiPriority w:val="9"/>
          <w:rsid w:val="003B190B"/>
          <w:rPr>
            <w:rFonts w:asciiTheme="majorHAnsi" w:eastAsiaTheme="majorEastAsia" w:hAnsiTheme="majorHAnsi" w:cstheme="majorBidi"/>
            <w:szCs w:val="21"/>
          </w:rPr>
        </w:style>
        <w:style w:type="paragraph" w:styleId="a4">
          <w:name w:val="header"/>
          <w:basedOn w:val="a"/>
          <w:link w:val="Char"/>
          <w:uiPriority w:val="99"/>
          <w:unhideWhenUsed/>
          <w:rsid w:val="00587A70"/>
          <w:pPr>
            <w:pBdr>
              <w:bottom w:val="single" w:sz="6" w:space="1" w:color="auto"/>
            </w:pBdr>
            <w:tabs>
              <w:tab w:val="center" w:pos="4153"/>
              <w:tab w:val="right" w:pos="8306"/>
            </w:tabs>
            <w:snapToGrid w:val="0"/>
            <w:jc w:val="center"/>
          </w:pPr>
          <w:rPr>
            <w:sz w:val="18"/>
            <w:szCs w:val="18"/>
          </w:rPr>
        </w:style>
        <w:style w:type="character" w:customStyle="1" w:styleId="Char">
          <w:name w:val="页眉 Char"/>
          <w:basedOn w:val="a0"/>
          <w:link w:val="a4"/>
          <w:uiPriority w:val="99"/>
          <w:rsid w:val="00587A70"/>
          <w:rPr>
            <w:sz w:val="18"/>
            <w:szCs w:val="18"/>
          </w:rPr>
        </w:style>
        <w:style w:type="paragraph" w:styleId="a5">
          <w:name w:val="footer"/>
          <w:basedOn w:val="a"/>
          <w:link w:val="Char0"/>
          <w:uiPriority w:val="99"/>
          <w:unhideWhenUsed/>
          <w:rsid w:val="00587A70"/>
          <w:pPr>
            <w:tabs>
              <w:tab w:val="center" w:pos="4153"/>
              <w:tab w:val="right" w:pos="8306"/>
            </w:tabs>
            <w:snapToGrid w:val="0"/>
            <w:jc w:val="left"/>
          </w:pPr>
          <w:rPr>
            <w:sz w:val="18"/>
            <w:szCs w:val="18"/>
          </w:rPr>
        </w:style>
        <w:style w:type="character" w:customStyle="1" w:styleId="Char0">
          <w:name w:val="页脚 Char"/>
          <w:basedOn w:val="a0"/>
          <w:link w:val="a5"/>
          <w:uiPriority w:val="99"/>
          <w:rsid w:val="00587A70"/>
          <w:rPr>
            <w:sz w:val="18"/>
            <w:szCs w:val="18"/>
          </w:rPr>
        </w:style>
        <w:style w:type="table" w:styleId="a6">
          <w:name w:val="Table Grid"/>
          <w:basedOn w:val="a1"/>
          <w:uiPriority w:val="39"/>
          <w:rsid w:val="00CF4303"/>
          <w:tblPr>
            <w:tblBorders>
              <w:top w:val="single" w:sz="4" w:space="0" w:color="auto"/>
              <w:left w:val="single" w:sz="4" w:space="0" w:color="auto"/>
              <w:bottom w:val="single" w:sz="4" w:space="0" w:color="auto"/>
              <w:right w:val="single" w:sz="4" w:space="0" w:color="auto"/>
              <w:insideH w:val="single" w:sz="4" w:space="0" w:color="auto"/>
              <w:insideV w:val="single" w:sz="4" w:space="0" w:color="auto"/>
            </w:tblBorders>
          </w:tblPr>
        </w:style>
      </w:styles>
    </pkg:xmlData>
  </pkg:part>
  <pkg:part pkg:name="/word/numbering.xml" pkg:contentType="application/vnd.openxmlformats-officedocument.wordprocessingml.numbering+xml">
    <pkg:xmlData>
      <w:numbering xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main" xmlns:wpc="http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas" xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006" xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:wp14="http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing" xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing" xmlns:w10="urn:schemas-microsoft-com:office:word" xmlns:w14="http://schemas.microsoft.com/office/word/2010/wordml" xmlns:w15="http://schemas.microsoft.com/office/word/2012/wordml" xmlns:wpg="http://schemas.microsoft.com/office/word/2010/wordprocessingGroup" xmlns:wpi="http://schemas.microsoft.com/office/word/2010/wordprocessingInk" xmlns:wne="http://schemas.microsoft.com/office/word/2006/wordml" xmlns:wps="http://schemas.microsoft.com/office/word/2010/wordprocessingShape" mc:Ignorable="w14 w15 wp14">
        <w:abstractNum w:abstractNumId="0" w15:restartNumberingAfterBreak="0">
          <w:nsid w:val="08752D28"/>
          <w:multiLevelType w:val="multilevel"/>
          <w:tmpl w:val="0409001F"/>
          <w:lvl w:ilvl="0">
            <w:start w:val="1"/>
            <w:numFmt w:val="decimal"/>
            <w:lvlText w:val="%1."/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:left="425" w:hanging="425"/>
            </w:pPr>
          </w:lvl>
          <w:lvl w:ilvl="1">
            <w:start w:val="1"/>
            <w:numFmt w:val="decimal"/>
            <w:lvlText w:val="%1.%2."/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:left="567" w:hanging="567"/>
            </w:pPr>
          </w:lvl>
          <w:lvl w:ilvl="2">
            <w:start w:val="1"/>
            <w:numFmt w:val="decimal"/>
            <w:lvlText w:val="%1.%2.%3."/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:left="709" w:hanging="709"/>
            </w:pPr>
          </w:lvl>
          <w:lvl w:ilvl="3">
            <w:start w:val="1"/>
            <w:numFmt w:val="decimal"/>
            <w:lvlText w:val="%1.%2.%3.%4."/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:left="851" w:hanging="851"/>
            </w:pPr>
          </w:lvl>
          <w:lvl w:ilvl="4">
            <w:start w:val="1"/>
            <w:numFmt w:val="decimal"/>
            <w:lvlText w:val="%1.%2.%3.%4.%5."/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:left="992" w:hanging="992"/>
            </w:pPr>
          </w:lvl>
          <w:lvl w:ilvl="5">
            <w:start w:val="1"/>
            <w:numFmt w:val="decimal"/>
            <w:lvlText w:val="%1.%2.%3.%4.%5.%6."/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:left="1134" w:hanging="1134"/>
            </w:pPr>
          </w:lvl>
          <w:lvl w:ilvl="6">
            <w:start w:val="1"/>
            <w:numFmt w:val="decimal"/>
            <w:lvlText w:val="%1.%2.%3.%4.%5.%6.%7."/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:left="1276" w:hanging="1276"/>
            </w:pPr>
          </w:lvl>
          <w:lvl w:ilvl="7">
            <w:start w:val="1"/>
            <w:numFmt w:val="decimal"/>
            <w:lvlText w:val="%1.%2.%3.%4.%5.%6.%7.%8."/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:left="1418" w:hanging="1418"/>
            </w:pPr>
          </w:lvl>
          <w:lvl w:ilvl="8">
            <w:start w:val="1"/>
            <w:numFmt w:val="decimal"/>
            <w:lvlText w:val="%1.%2.%3.%4.%5.%6.%7.%8.%9."/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:left="1559" w:hanging="1559"/>
            </w:pPr>
          </w:lvl>
        </w:abstractNum>
        <w:abstractNum w:abstractNumId="1" w15:restartNumberingAfterBreak="0">
          <w:nsid w:val="4CBE598D"/>
          <w:multiLevelType w:val="multilevel"/>
          <w:tmpl w:val="0409001F"/>
          <w:lvl w:ilvl="0">
            <w:start w:val="1"/>
            <w:numFmt w:val="decimal"/>
            <w:lvlText w:val="%1."/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:left="425" w:hanging="425"/>
            </w:pPr>
          </w:lvl>
          <w:lvl w:ilvl="1">
            <w:start w:val="1"/>
            <w:numFmt w:val="decimal"/>
            <w:lvlText w:val="%1.%2."/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:left="567" w:hanging="567"/>
            </w:pPr>
          </w:lvl>
          <w:lvl w:ilvl="2">
            <w:start w:val="1"/>
            <w:numFmt w:val="decimal"/>
            <w:lvlText w:val="%1.%2.%3."/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:left="709" w:hanging="709"/>
            </w:pPr>
          </w:lvl>
          <w:lvl w:ilvl="3">
            <w:start w:val="1"/>
            <w:numFmt w:val="decimal"/>
            <w:lvlText w:val="%1.%2.%3.%4."/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:left="851" w:hanging="851"/>
            </w:pPr>
          </w:lvl>
          <w:lvl w:ilvl="4">
            <w:start w:val="1"/>
            <w:numFmt w:val="decimal"/>
            <w:lvlText w:val="%1.%2.%3.%4.%5."/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:left="992" w:hanging="992"/>
            </w:pPr>
          </w:lvl>
          <w:lvl w:ilvl="5">
            <w:start w:val="1"/>
            <w:numFmt w:val="decimal"/>
            <w:lvlText w:val="%1.%2.%3.%4.%5.%6."/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:left="1134" w:hanging="1134"/>
            </w:pPr>
          </w:lvl>
          <w:lvl w:ilvl="6">
            <w:start w:val="1"/>
            <w:numFmt w:val="decimal"/>
            <w:lvlText w:val="%1.%2.%3.%4.%5.%6.%7."/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:left="1276" w:hanging="1276"/>
            </w:pPr>
          </w:lvl>
          <w:lvl w:ilvl="7">
            <w:start w:val="1"/>
            <w:numFmt w:val="decimal"/>
            <w:lvlText w:val="%1.%2.%3.%4.%5.%6.%7.%8."/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:left="1418" w:hanging="1418"/>
            </w:pPr>
          </w:lvl>
          <w:lvl w:ilvl="8">
            <w:start w:val="1"/>
            <w:numFmt w:val="decimal"/>
            <w:lvlText w:val="%1.%2.%3.%4.%5.%6.%7.%8.%9."/>
            <w:lvlJc w:val="left"/>
            <w:pPr>
              <w:ind w:left="1559" w:hanging="1559"/>
            </w:pPr>
          </w:lvl>
        </w:abstractNum>
        <w:num w:numId="1">
          <w:abstractNumId w:val="0"/>
        </w:num>
        <w:num w:numId="2">
          <w:abstractNumId w:val="1"/>
        </w:num>
      </w:numbering>
    </pkg:xmlData>
  </pkg:part>
  <pkg:part pkg:name="/docProps/core.xml" pkg:contentType="application/vnd.openxmlformats-package.core-properties+xml" pkg:padding="256">
    <pkg:xmlData>
      <cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dcmitype="http://purl.org/dc/dcmitype/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <dc:title/>
        <dc:subject/>
        <dc:creator>张三</dc:creator>
        <cp:keywords/>
        <dc:description/>
        <cp:lastModifiedBy>张三</cp:lastModifiedBy>
        <cp:revision>8</cp:revision>
        <dcterms:created xsi:type="dcterms:W3CDTF">2016-02-26T07:21:00Z</dcterms:created>
        <dcterms:modified xsi:type="dcterms:W3CDTF">2016-03-01T02:59:00Z</dcterms:modified>
      </cp:coreProperties>
    </pkg:xmlData>
  </pkg:part>
  <pkg:part pkg:name="/word/fontTable.xml" pkg:contentType="application/vnd.openxmlformats-officedocument.wordprocessingml.fontTable+xml">
    <pkg:xmlData>
      <w:fonts xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main" xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" xmlns:w14="http://schemas.microsoft.com/office/word/2010/wordml" xmlns:w15="http://schemas.microsoft.com/office/word/2012/wordml" mc:Ignorable="w14 w15">
        <w:font w:name="Calibri">
          <w:panose1 w:val="020F0502020204030204"/>
          <w:charset w:val="00"/>
          <w:family w:val="swiss"/>
          <w:pitch w:val="variable"/>
          <w:sig w:usb0="E00002FF" w:usb1="4000ACFF" w:usb2="00000001" w:usb3="00000000" w:csb0="0000019F" w:csb1="00000000"/>
        </w:font>
        <w:font w:name="宋体">
          <w:altName w:val="SimSun"/>
          <w:panose1 w:val="02010600030101010101"/>
          <w:charset w:val="86"/>
          <w:family w:val="auto"/>
          <w:pitch w:val="variable"/>
          <w:sig w:usb0="00000003" w:usb1="288F0000" w:usb2="00000016" w:usb3="00000000" w:csb0="00040001" w:csb1="00000000"/>
        </w:font>
        <w:font w:name="Times New Roman">
          <w:panose1 w:val="02020603050405020304"/>
          <w:charset w:val="00"/>
          <w:family w:val="roman"/>
          <w:pitch w:val="variable"/>
          <w:sig w:usb0="E0002AFF" w:usb1="C0007841" w:usb2="00000009" w:usb3="00000000" w:csb0="000001FF" w:csb1="00000000"/>
        </w:font>
        <w:font w:name="Calibri Light">
          <w:panose1 w:val="020F0302020204030204"/>
          <w:charset w:val="00"/>
          <w:family w:val="swiss"/>
          <w:pitch w:val="variable"/>
          <w:sig w:usb0="A00002EF" w:usb1="4000207B" w:usb2="00000000" w:usb3="00000000" w:csb0="0000019F" w:csb1="00000000"/>
        </w:font>
      </w:fonts>
    </pkg:xmlData>
  </pkg:part>
  <pkg:part pkg:name="/docProps/app.xml" pkg:contentType="application/vnd.openxmlformats-officedocument.extended-properties+xml" pkg:padding="256">
    <pkg:xmlData>
      <Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/extended-properties" xmlns:vt="http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes">
        <Template>Normal.dotm</Template>
        <TotalTime>14</TotalTime>
        <Pages>1</Pages>
        <Words>22</Words>
        <Characters>128</Characters>
        <Application>Microsoft Office Word</Application>
        <DocSecurity>0</DocSecurity>
        <Lines>1</Lines>
        <Paragraphs>1</Paragraphs>
        <ScaleCrop>false</ScaleCrop>
        <Company>中国</Company>
        <LinksUpToDate>false</LinksUpToDate>
        <CharactersWithSpaces>149</CharactersWithSpaces>
        <SharedDoc>false</SharedDoc>
        <HyperlinksChanged>false</HyperlinksChanged>
        <AppVersion>15.0000</AppVersion>
      </Properties>
    </pkg:xmlData>
  </pkg:part>
</pkg:package>
